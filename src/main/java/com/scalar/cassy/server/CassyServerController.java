package com.scalar.cassy.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.config.RestoreType;
import com.scalar.cassy.db.BackupHistory;
import com.scalar.cassy.db.BackupHistoryRecord;
import com.scalar.cassy.db.ClusterInfo;
import com.scalar.cassy.db.ClusterInfoRecord;
import com.scalar.cassy.db.RestoreHistory;
import com.scalar.cassy.db.RestoreHistoryRecord;
import com.scalar.cassy.exception.DatabaseException;
import com.scalar.cassy.jmx.JmxManager;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc.CassyImplBase;
import com.scalar.cassy.rpc.ClusterListingRequest;
import com.scalar.cassy.rpc.ClusterListingResponse;
import com.scalar.cassy.rpc.ClusterRegistrationRequest;
import com.scalar.cassy.rpc.ClusterRegistrationResponse;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.rpc.RestoreRequest;
import com.scalar.cassy.rpc.RestoreResponse;
import com.scalar.cassy.rpc.RestoreStatusListingRequest;
import com.scalar.cassy.rpc.RestoreStatusListingResponse;
import com.scalar.cassy.service.ApplicationPauser;
import com.scalar.cassy.service.BackupKey;
import com.scalar.cassy.service.BackupServiceMaster;
import com.scalar.cassy.service.MetadataDbBackupService;
import com.scalar.cassy.service.RestoreServiceMaster;
import com.scalar.cassy.util.ConnectionUtil;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public final class CassyServerController extends CassyImplBase {
  private static final Logger logger = LoggerFactory.getLogger(CassyServerController.class);
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final CassyServerConfig config;
  private final BlockingQueue<RemoteCommandContext> commandQueue;
  private final MetadataDbBackupService metadataDbBackupService;
  private static final String NO_CASSANDRA_HOST = "cassandra_host is not set correctly.";
  private static final String NODES_UNAVAILABLE = "All the required nodes are not available.";
  private static final String NO_SNAPSHOT_ID =
      "snapshot_id must be specified when taking incremental backups.";
  private static final String NON_EMPTY_SNAPSHOT_ID =
      "snapshot_id must be empty when taking snapshots.";
  private static final String INVALID_REQUEST = "The request is invalid or inconsistent.";
  private static final String CLUSTER_NOT_FOUND = "The specified cluster cannot be found.";
  private static final String NON_CLUSTER_BACKUP =
      "The specified backup is not a cluster-wide backup.";

  @Inject
  public CassyServerController(
      CassyServerConfig config,
      BlockingQueue<RemoteCommandContext> commandQueue,
      MetadataDbBackupService metadataDbBackupService) {
    this.config = config;
    this.commandQueue = commandQueue;
    this.metadataDbBackupService = metadataDbBackupService;
  }

  @Override
  public void registerCluster(
      ClusterRegistrationRequest request,
      StreamObserver<ClusterRegistrationResponse> responseObserver) {
    if (request.getCassandraHost().isEmpty()) {
      setError(responseObserver, Status.INVALID_ARGUMENT, NO_CASSANDRA_HOST);
      return;
    }

    ClusterInfoRecord record;
    Connection connection = null;

    try {
      JmxManager jmx = getJmx(request.getCassandraHost(), config.getJmxPort());
      if (!areAllNodesAlive(jmx)) {
        setError(responseObserver, Status.UNAVAILABLE, NODES_UNAVAILABLE);
        return;
      }

      String clusterId = jmx.getClusterName() + "-" + UUID.randomUUID().toString();
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      ClusterInfo clusterInfo = new ClusterInfo(connection);
      clusterInfo.insert(
          clusterId,
          jmx.getClusterName(),
          jmx.getLiveNodes(),
          jmx.getKeyspaces(),
          jmx.getAllDataFileLocations().get(0));
      record = clusterInfo.selectByClusterId(clusterId).get();
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    ClusterRegistrationResponse response =
        ClusterRegistrationResponse.newBuilder()
            .setClusterId(record.getClusterId())
            .setClusterName(record.getClusterName())
            .addAllTargetIps(record.getTargetIps())
            .addAllKeyspaces(record.getKeyspaces())
            .setDataDir(record.getDataDir())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void listClusters(
      ClusterListingRequest request, StreamObserver<ClusterListingResponse> responseObserver) {
    List<ClusterInfoRecord> clusters = new ArrayList<>();
    Connection connection = null;

    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      ClusterInfo clusterInfo = new ClusterInfo(connection);
      if (request.getClusterId().isEmpty()) {
        int limit = request.getLimit() == 0 ? -1 : request.getLimit();
        clusters.addAll(clusterInfo.selectRecent(limit));
      } else {
        clusterInfo.selectByClusterId(request.getClusterId()).ifPresent(c -> clusters.add(c));
      }
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    ClusterListingResponse.Builder builder = ClusterListingResponse.newBuilder();
    clusters.forEach(
        c -> {
          ClusterListingResponse.Entry entry =
              ClusterListingResponse.Entry.newBuilder()
                  .setClusterId(c.getClusterId())
                  .setClusterName(c.getClusterName())
                  .addAllTargetIps(c.getTargetIps())
                  .addAllKeyspaces(c.getKeyspaces())
                  .setDataDir(c.getDataDir())
                  .setCreatedAt(c.getCreatedAt())
                  .setUpdatedAt(c.getUpdatedAt())
                  .build();
          builder.addEntries(entry);
        });

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void listBackups(
      BackupListingRequest request, StreamObserver<BackupListingResponse> responseObserver) {
    List<BackupHistoryRecord> records;
    Connection connection = null;

    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      BackupHistory backupHistory = new BackupHistory(connection);
      records = backupHistory.selectRecentSnapshots(request);
    } catch (IllegalArgumentException e) {
      setError(responseObserver, Status.INVALID_ARGUMENT, e);
      return;
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    BackupListingResponse.Builder builder = BackupListingResponse.newBuilder();
    records.forEach(
        r -> {
          BackupListingResponse.Entry entry =
              BackupListingResponse.Entry.newBuilder()
                  .setSnapshotId(r.getSnapshotId())
                  .setClusterId(r.getClusterId())
                  .setTargetIp(r.getTargetIp())
                  .setCreatedAt(r.getCreatedAt())
                  .setUpdatedAt(r.getUpdatedAt())
                  .setBackupType(r.getBackupType().get())
                  .setStatus(OperationStatus.forNumber(r.getStatus().getNumber()))
                  .build();
          builder.addEntries(entry);
        });

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  public void listRestoreStatuses(
      RestoreStatusListingRequest request,
      StreamObserver<RestoreStatusListingResponse> responseObserver) {
    List<RestoreHistoryRecord> records;
    Connection connection = null;

    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      RestoreHistory restoreHistory = new RestoreHistory(connection);
      records = restoreHistory.selectRecent(request);
    } catch (IllegalArgumentException e) {
      setError(responseObserver, Status.INVALID_ARGUMENT, e);
      return;
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    RestoreStatusListingResponse.Builder builder = RestoreStatusListingResponse.newBuilder();
    records.forEach(
        r -> {
          RestoreStatusListingResponse.Entry entry =
              RestoreStatusListingResponse.Entry.newBuilder()
                  .setSnapshotId(r.getSnapshotId())
                  .setTargetIp(r.getTargetIp())
                  .setCreatedAt(r.getCreatedAt())
                  .setUpdatedAt(r.getUpdatedAt())
                  .setRestoreType(r.getRestoreType().get())
                  .setStatus(OperationStatus.forNumber(r.getStatus().getNumber()))
                  .build();
          builder.addEntries(entry);
        });

    responseObserver.onNext(builder.setClusterId(request.getClusterId()).build());
    responseObserver.onCompleted();
  }

  @Override
  public void takeBackup(BackupRequest request, StreamObserver<BackupResponse> responseObserver) {
    Connection connection = null;
    final String snapshotId = createSnapshotId(request);
    BackupType type = BackupType.getByType(request.getBackupType());
    BackupHistory backupHistory = null;
    List<BackupKey> backupKeys = null;

    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());

      if (!isValid(request)) {
        throw new IllegalArgumentException(INVALID_REQUEST);
      }

      ClusterInfo clusterInfo = new ClusterInfo(connection);
      Optional<ClusterInfoRecord> cluster = getCluster(request.getClusterId(), clusterInfo);
      if (!cluster.isPresent()) {
        throw new IllegalArgumentException(CLUSTER_NOT_FOUND);
      }

      if (!areTargetsAlive(cluster.get().getTargetIps(), config.getJmxPort())) {
        throw new DatabaseException(NODES_UNAVAILABLE);
      }

      backupHistory = new BackupHistory(connection);
      backupKeys = getBackupKeys(snapshotId, request.getTargetIpsList(), cluster.get());
      updateBackupStatus(backupHistory, backupKeys, type, OperationStatus.INITIALIZED);

      BackupServiceMaster master =
          new BackupServiceMaster(
              config,
              cluster.get(),
              new RemoteCommandExecutor(),
              new ApplicationPauser(config.getSrvServiceUrl()));
      List<RemoteCommandContext> futures = master.takeBackup(backupKeys, type);

      updateBackupStatus(backupHistory, backupKeys, type, OperationStatus.STARTED);
      commandQueue.addAll(futures);
    } catch (IllegalArgumentException e) {
      setError(responseObserver, Status.INVALID_ARGUMENT, e.getMessage());
      return;
    } catch (Exception e) {
      updateBackupStatus(backupHistory, backupKeys, type, OperationStatus.FAILED);
      setError(responseObserver, Status.INTERNAL, e.getMessage());
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    // it will backup metadata database to a specified storage
    backupMetadata();

    BackupResponse.Builder builder =
        BackupResponse.newBuilder()
            .setSnapshotId(snapshotId)
            .setStatus(OperationStatus.STARTED)
            .setClusterId(backupKeys.get(0).getClusterId())
            .addAllTargetIps(
                backupKeys.stream().map(k -> k.getTargetIp()).collect(Collectors.toList()))
            .setSnapshotId(snapshotId)
            .setCreatedAt(backupKeys.get(0).getCreatedAt())
            .setBackupType(type.get());

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    Connection connection = null;
    BackupHistory backupHistory = null;
    RestoreHistory restoreHistory = null;
    RestoreType type = RestoreType.getByType(request.getRestoreType());
    List<BackupKey> backupKeys = Collections.emptyList();

    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      backupHistory = new BackupHistory(connection);
      restoreHistory = new RestoreHistory(connection);

      if (!isValid(request, backupHistory)) {
        throw new IllegalArgumentException(INVALID_REQUEST);
      }

      ClusterInfo clusterInfo = new ClusterInfo(connection);
      Optional<ClusterInfoRecord> cluster = getCluster(request.getClusterId(), clusterInfo);
      if (!cluster.isPresent()) {
        throw new IllegalArgumentException(CLUSTER_NOT_FOUND);
      }

      backupKeys =
          getBackupKeys(request.getSnapshotId(), request.getTargetIpsList(), cluster.get());
      updateRestoreStatus(restoreHistory, backupKeys, type, OperationStatus.INITIALIZED);

      RestoreServiceMaster master =
          new RestoreServiceMaster(config, cluster.get(), new RemoteCommandExecutor());
      List<RemoteCommandContext> futures =
          master.restoreBackup(backupKeys, type, request.getSnapshotOnly());

      updateRestoreStatus(restoreHistory, backupKeys, type, OperationStatus.STARTED);
      commandQueue.addAll(futures);
    } catch (IllegalArgumentException e) {
      setError(responseObserver, Status.INVALID_ARGUMENT, e.getMessage());
      return;
    } catch (Exception e) {
      updateRestoreStatus(restoreHistory, backupKeys, type, OperationStatus.FAILED);
      setError(responseObserver, Status.INTERNAL, e.getMessage());
      return;
    } finally {
      ConnectionUtil.close(connection);
    }

    RestoreResponse.Builder builder =
        RestoreResponse.newBuilder()
            .setStatus(OperationStatus.STARTED)
            .setClusterId(backupKeys.get(0).getClusterId())
            .addAllTargetIps(
                backupKeys.stream().map(k -> k.getTargetIp()).collect(Collectors.toList()))
            .setSnapshotId(request.getSnapshotId())
            .setRestoreType(type.get())
            .setSnapshotOnly(request.getSnapshotOnly());

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @VisibleForTesting
  JmxManager getJmx(String ip, int port) {
    return new JmxManager(ip, port);
  }

  private boolean areAllNodesAlive(JmxManager jmx) {
    if (jmx.getJoiningNodes().isEmpty()
        && jmx.getMovingNodes().isEmpty()
        && jmx.getLeavingNodes().isEmpty()
        && jmx.getUnreachableNodes().isEmpty()
        && jmx.getLiveNodes().size() > 0) {
      return true;
    }
    return false;
  }

  private boolean isValid(BackupRequest request) {
    if (request.getSnapshotId().isEmpty()
        && request.getBackupType() == BackupType.NODE_INCREMENTAL.get()) {
      logger.error(NO_SNAPSHOT_ID);
      return false;
    }

    if (!request.getSnapshotId().isEmpty()
        && request.getBackupType() != BackupType.NODE_INCREMENTAL.get()) {
      logger.error(NON_EMPTY_SNAPSHOT_ID);
      return false;
    }
    return true;
  }

  private boolean isValid(RestoreRequest request, BackupHistory backupHistory) {
    // Return invalid if node-level backups are used to restore a cluster
    if (request.getRestoreType() == RestoreType.CLUSTER.get()) {
      BackupListingRequest listingRequest =
          BackupListingRequest.newBuilder().setSnapshotId(request.getSnapshotId()).build();
      List<BackupHistoryRecord> records = backupHistory.selectRecentSnapshots(listingRequest);
      if (records.get(records.size() - 1).getBackupType() == BackupType.NODE_SNAPSHOT) {
        logger.error(NON_CLUSTER_BACKUP);
        return false;
      }
    }
    return true;
  }

  private <T> Optional<ClusterInfoRecord> getCluster(String clusterId, ClusterInfo clusterInfo) {
    return clusterInfo.selectByClusterId(clusterId);
  }

  private <T> boolean areTargetsAlive(List<String> ips, int port) {
    try {
      JmxManager jmx = getJmx(ips.get(0), port);
      if (!jmx.getLiveNodes().containsAll(ips)) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private String createSnapshotId(BackupRequest request) {
    return request.getSnapshotId().isEmpty()
        ? UUID.randomUUID().toString()
        : request.getSnapshotId();
  }

  private List<BackupKey> getBackupKeys(
      String snapshotId, List<String> targets, ClusterInfoRecord record) {
    List<BackupKey> backupKeys = new ArrayList<>();
    long currentTime = System.currentTimeMillis();

    if (targets.isEmpty()) {
      targets = record.getTargetIps();
    }

    targets.forEach(
        ip -> {
          BackupKey.Builder keyBuilder =
              BackupKey.newBuilder()
                  .snapshotId(snapshotId)
                  .clusterId(record.getClusterId())
                  .targetIp(ip)
                  .createdAt(currentTime);
          backupKeys.add(keyBuilder.build());
        });

    return backupKeys;
  }

  private void updateBackupStatus(
      BackupHistory backupHistory,
      List<BackupKey> backupKeys,
      BackupType type,
      OperationStatus status) {
    if (backupHistory == null) {
      return;
    }
    if (status == OperationStatus.INITIALIZED) {
      backupKeys.forEach(backupKey -> backupHistory.insert(backupKey, type, status));
    } else {
      backupKeys.forEach(backupKey -> backupHistory.update(backupKey, status));
    }
  }

  private void updateRestoreStatus(
      RestoreHistory restoreHistory,
      List<BackupKey> backupKeys,
      RestoreType type,
      OperationStatus status) {
    if (restoreHistory == null) {
      return;
    }
    if (status == OperationStatus.INITIALIZED) {
      backupKeys.forEach(backupKey -> restoreHistory.insert(backupKey, type, status));
    } else {
      backupKeys.forEach(backupKey -> restoreHistory.update(backupKey, status));
    }
  }

  private void backupMetadata() {
    executorService.submit(
        () -> {
          while (true) {
            // it will wait for statuses to be updated
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            if (commandQueue.isEmpty()) {
              break;
            }
          }
          try {
            metadataDbBackupService.backup().get();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // ignore errors
            logger.warn(e.getMessage(), e);
          } catch (Exception e) {
            // ignore errors
            logger.warn(e.getMessage(), e);
          }
        });
  }

  private <T> void setError(StreamObserver<T> responseObserver, Status status, String message) {
    logger.error(message);
    responseObserver.onError(status.withDescription(message).asRuntimeException());
  }

  private <T> void setError(StreamObserver<T> responseObserver, Status status, Throwable e) {
    logger.error(e.getMessage(), e);
    responseObserver.onError(status.withCause(e).asRuntimeException());
  }
}
