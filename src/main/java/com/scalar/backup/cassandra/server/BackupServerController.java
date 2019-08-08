package com.scalar.backup.cassandra.server;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.db.BackupHistoryRecord;
import com.scalar.backup.cassandra.db.ClusterInfoRecord;
import com.scalar.backup.cassandra.db.DatabaseAccessor;
import com.scalar.backup.cassandra.db.RestoreHistoryRecord;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandContext;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandExecutor;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.BackupListingResponse;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import com.scalar.backup.cassandra.rpc.BackupResponse;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import com.scalar.backup.cassandra.rpc.ClusterListingRequest;
import com.scalar.backup.cassandra.rpc.ClusterListingResponse;
import com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest;
import com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
import com.scalar.backup.cassandra.rpc.RestoreResponse;
import com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest;
import com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse;
import com.scalar.backup.cassandra.service.ApplicationPauser;
import com.scalar.backup.cassandra.service.BackupKey;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
import com.scalar.backup.cassandra.service.RestoreServiceMaster;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public final class BackupServerController extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = LoggerFactory.getLogger(BackupServerController.class);
  private final BackupServerConfig config;
  private final DatabaseAccessor database;
  private final BlockingQueue<RemoteCommandContext> commandQueue;
  private static final String NO_CASSANDRA_HOST = "cassandra_host is not set correctly.";
  private static final String NODES_UNAVAILABLE = "All the required nodes are not available.";
  private static final String NO_SNAPSHOT_ID =
      "snapshot_id must be specified when taking incremental backups.";
  private static final String NON_EMPTY_SNAPSHOT_ID =
      "snapshot_id must be empty when taking snapshots.";
  private static final String CLUSTER_NOT_FOUND = "The specified cluster not found.";
  private static final String NON_CLUSTER_BACKUP =
      "The specified backup is not a cluster-wide backup.";

  public BackupServerController(
      BackupServerConfig config,
      DatabaseAccessor database,
      BlockingQueue<RemoteCommandContext> commandQueue) {
    this.config = config;
    this.database = database;
    this.commandQueue = commandQueue;
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
    try {
      JmxManager jmx = getJmx(request.getCassandraHost(), config.getJmxPort());
      if (!areAllNodesAlive(jmx)) {
        setError(responseObserver, Status.UNAVAILABLE, NODES_UNAVAILABLE);
        return;
      }

      database
          .getClusterInfo()
          .upsert(
              jmx.getClusterName(),
              jmx.getLiveNodes(),
              jmx.getKeyspaces(),
              jmx.getAllDataFileLocations().get(0));
      record = database.getClusterInfo().selectByClusterId(jmx.getClusterName()).get();
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
    }

    ClusterRegistrationResponse response =
        ClusterRegistrationResponse.newBuilder()
            .setClusterId(record.getClusterId())
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
    if (request.getClusterId().isEmpty()) {
      int limit = request.getLimit() == 0 ? -1 : request.getLimit();
      clusters.addAll(database.getClusterInfo().selectRecent(limit));
    } else {
      database
          .getClusterInfo()
          .selectByClusterId(request.getClusterId())
          .ifPresent(c -> clusters.add(c));
    }

    ClusterListingResponse.Builder builder = ClusterListingResponse.newBuilder();
    clusters.forEach(
        c -> {
          ClusterListingResponse.Entry entry =
              ClusterListingResponse.Entry.newBuilder()
                  .setClusterId(c.getClusterId())
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
    try {
      records = database.getBackupHistory().selectRecentSnapshots(request);
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
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
    try {
      records = database.getRestoreHistory().selectRecent(request);
    } catch (IllegalArgumentException e) {
      setError(responseObserver, Status.INVALID_ARGUMENT, e);
      return;
    } catch (Exception e) {
      setError(responseObserver, Status.INTERNAL, e);
      return;
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
    if (!isValidOrSetError(request, responseObserver)) {
      return;
    }

    Optional<ClusterInfoRecord> clusterInfo =
        getClusterOrSetError(request.getClusterId(), responseObserver);
    if (!clusterInfo.isPresent()) {
      return;
    }

    if (!areTargetsAliveOrSetError(
        clusterInfo.get().getTargetIps(), config.getJmxPort(), responseObserver)) {
      return;
    }

    final String snapshotId = createSnapshotId(request);
    List<BackupKey> backupKeys =
        getBackupKeys(snapshotId, request.getTargetIpsList(), clusterInfo.get());
    BackupType type = BackupType.getByType(request.getBackupType());
    BackupResponse.Builder builder = BackupResponse.newBuilder().setSnapshotId(snapshotId);

    try {
      updateBackupStatus(backupKeys, type, OperationStatus.INITIALIZED);
      BackupServiceMaster master =
          new BackupServiceMaster(
              config,
              clusterInfo.get(),
              new RemoteCommandExecutor(),
              new ApplicationPauser(config.getSrvServiceUrl()));
      List<RemoteCommandContext> futures = master.takeBackup(backupKeys, type);
      updateBackupStatus(backupKeys, type, OperationStatus.STARTED);
      commandQueue.addAll(futures);
    } catch (Exception e) {
      builder.setStatus(OperationStatus.FAILED);
      updateBackupStatus(backupKeys, type, OperationStatus.FAILED);
    }

    builder
        .setStatus(OperationStatus.STARTED)
        .setClusterId(backupKeys.get(0).getClusterId())
        .addAllTargetIps(backupKeys.stream().map(k -> k.getTargetIp()).collect(Collectors.toList()))
        .setSnapshotId(snapshotId)
        .setCreatedAt(backupKeys.get(0).getCreatedAt())
        .setBackupType(type.get());

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    if (!isValidOrSetError(request, responseObserver)) {
      return;
    }
    Optional<ClusterInfoRecord> clusterInfo =
        getClusterOrSetError(request.getClusterId(), responseObserver);
    if (!clusterInfo.isPresent()) {
      return;
    }

    List<BackupKey> backupKeys =
        getBackupKeys(request.getSnapshotId(), request.getTargetIpsList(), clusterInfo.get());
    RemoteCommandExecutor executor = new RemoteCommandExecutor();
    RestoreType type = RestoreType.getByType(request.getRestoreType());
    RestoreResponse.Builder builder = RestoreResponse.newBuilder();

    try {
      updateRestoreStatus(backupKeys, type, OperationStatus.INITIALIZED);
      RestoreServiceMaster master = new RestoreServiceMaster(config, clusterInfo.get(), executor);
      List<RemoteCommandContext> futures = master.restoreBackup(backupKeys, type);
      updateRestoreStatus(backupKeys, type, OperationStatus.STARTED);
      commandQueue.addAll(futures);
    } catch (Exception e) {
      builder.setStatus(OperationStatus.FAILED);
      updateRestoreStatus(backupKeys, type, OperationStatus.FAILED);
    }

    builder
        .setStatus(OperationStatus.STARTED)
        .setClusterId(backupKeys.get(0).getClusterId())
        .addAllTargetIps(backupKeys.stream().map(k -> k.getTargetIp()).collect(Collectors.toList()))
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

  private boolean isValidOrSetError(
      BackupRequest request, StreamObserver<BackupResponse> responseObserver) {
    if (request.getSnapshotId().isEmpty()
        && request.getBackupType() == BackupType.NODE_INCREMENTAL.get()) {
      setError(responseObserver, Status.INVALID_ARGUMENT, NO_SNAPSHOT_ID);
      return false;
    }

    if (!request.getSnapshotId().isEmpty()
        && request.getBackupType() != BackupType.NODE_INCREMENTAL.get()) {
      setError(responseObserver, Status.INVALID_ARGUMENT, NON_EMPTY_SNAPSHOT_ID);
      return false;
    }
    return true;
  }

  private boolean isValidOrSetError(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    // Return invalid if node-level backups are used to restore a cluster
    if (request.getRestoreType() == RestoreType.CLUSTER.get()) {
      BackupListingRequest listingRequest =
          BackupListingRequest.newBuilder().setSnapshotId(request.getSnapshotId()).build();
      List<BackupHistoryRecord> records =
          database.getBackupHistory().selectRecentSnapshots(listingRequest);
      if (records.get(records.size() - 1).getBackupType() == BackupType.NODE_SNAPSHOT) {
        setError(responseObserver, Status.INVALID_ARGUMENT, NON_CLUSTER_BACKUP);
        return false;
      }
    }
    return true;
  }

  private <T> Optional<ClusterInfoRecord> getClusterOrSetError(
      String clusterId, StreamObserver<T> responseObserver) {
    Optional<ClusterInfoRecord> clusterInfo =
        database.getClusterInfo().selectByClusterId(clusterId);
    if (!clusterInfo.isPresent()) {
      setError(responseObserver, Status.NOT_FOUND, CLUSTER_NOT_FOUND);
    }
    return clusterInfo;
  }

  private <T> boolean areTargetsAliveOrSetError(
      List<String> ips, int port, StreamObserver<T> responseObserver) {
    try {
      JmxManager jmx = getJmx(ips.get(0), port);
      if (!jmx.getLiveNodes().containsAll(ips)) {
        setError(responseObserver, Status.UNAVAILABLE, NODES_UNAVAILABLE);
        return false;
      }
    } catch (Exception e) {
      setError(responseObserver, Status.UNAVAILABLE, e);
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
      List<BackupKey> backupKeys, BackupType type, OperationStatus status) {
    if (status == OperationStatus.INITIALIZED) {
      backupKeys.forEach(backupKey -> database.getBackupHistory().insert(backupKey, type, status));
    } else {
      backupKeys.forEach(backupKey -> database.getBackupHistory().update(backupKey, status));
    }
  }

  private void updateRestoreStatus(
      List<BackupKey> backupKeys, RestoreType type, OperationStatus status) {
    if (status == OperationStatus.INITIALIZED) {
      backupKeys.forEach(backupKey -> database.getRestoreHistory().insert(backupKey, type, status));
    } else {
      backupKeys.forEach(backupKey -> database.getRestoreHistory().update(backupKey, status));
    }
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
