package com.scalar.backup.cassandra.server;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.db.BackupHistoryRecord;
import com.scalar.backup.cassandra.db.ClusterInfoRecord;
import com.scalar.backup.cassandra.db.DatabaseAccessor;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandContext;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandExecutor;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.BackupListingResponse;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import com.scalar.backup.cassandra.rpc.BackupResponse;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import com.scalar.backup.cassandra.rpc.ClusterListingResponse;
import com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest;
import com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse;
import com.scalar.backup.cassandra.rpc.NodeListingRequest;
import com.scalar.backup.cassandra.rpc.NodeListingResponse;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
import com.scalar.backup.cassandra.rpc.RestoreResponse;
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
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public final class BackupServerController extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = LoggerFactory.getLogger(BackupServerController.class);
  private final BackupServerConfig config;
  private final DatabaseAccessor database;
  private final BlockingQueue<RemoteCommandContext> futureQueue;

  public BackupServerController(
      BackupServerConfig config,
      DatabaseAccessor database,
      BlockingQueue<RemoteCommandContext> futureQueue) {
    this.config = config;
    this.database = database;
    this.futureQueue = futureQueue;
  }

  @Override
  public void registerCluster(
      ClusterRegistrationRequest request,
      StreamObserver<ClusterRegistrationResponse> responseObserver) {
    if (request.getCassandraHost().isEmpty() || request.getJmxPort() == 0) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("cassandra_host or jmx_port is not set correctly.")
              .asRuntimeException());
      return;
    }

    ClusterInfoRecord record;
    try {
      JmxManager jmx = getJmx(request.getCassandraHost(), request.getJmxPort());
      if (!areAllNodesAlive(jmx)) {
        responseObserver.onError(
            Status.UNAVAILABLE
                .withDescription("This method is allowed only when all the nodes are alive.")
                .asRuntimeException());
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
      logger.error(e.getMessage(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asRuntimeException());
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
  public void showClusters(
      com.google.protobuf.Empty request, StreamObserver<ClusterListingResponse> responseObserver) {
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());
    ClusterListingResponse response =
        ClusterListingResponse.newBuilder().addClusterId(jmx.getClusterName()).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void listNodes(
      NodeListingRequest request, StreamObserver<NodeListingResponse> responseObserver) {
    NodeListingResponse.Builder builder = NodeListingResponse.newBuilder();
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());

    addNodes(builder, jmx.getLiveNodes(), NodeListingResponse.Entry.NodeStatus.LIVE);
    addNodes(builder, jmx.getJoiningNodes(), NodeListingResponse.Entry.NodeStatus.JOINING);
    addNodes(builder, jmx.getLeavingNodes(), NodeListingResponse.Entry.NodeStatus.LEAVING);
    addNodes(builder, jmx.getMovingNodes(), NodeListingResponse.Entry.NodeStatus.MOVING);
    addNodes(builder, jmx.getUnreachableNodes(), NodeListingResponse.Entry.NodeStatus.UNREACHABLE);

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void listBackups(
      BackupListingRequest request, StreamObserver<BackupListingResponse> responseObserver) {
    List<BackupHistoryRecord> records = null;
    try {
      records = database.getBackupHistory().selectRecentSnapshots(request);
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withCause(e).asRuntimeException());
      return;
    }

    BackupListingResponse.Builder builder = BackupListingResponse.newBuilder();
    records.forEach(
        r -> {
          BackupListingResponse.Entry entry =
              BackupListingResponse.Entry.newBuilder()
                  .setSnapshotId(r.getSnapshotId())
                  .setIncrementalId(r.getIncrementalId())
                  .setClusterId(r.getClusterId())
                  .setTargetIp(r.getTargetIp())
                  .setBackupType(r.getBackupType().get())
                  .setStatus(OperationStatus.forNumber(r.getStatus().getNumber()))
                  .setTimestamp(r.getUpdatedAt())
                  .build();
          builder.addEntries(entry);
        });

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void takeBackup(BackupRequest request, StreamObserver<BackupResponse> responseObserver) {
    if (request.getSnapshotId().isEmpty()
        && request.getBackupType() == BackupType.NODE_INCREMENTAL.get()) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("snapshot_id must be specified when taking incremental backups.")
              .asRuntimeException());
      return;
    }
    if (!request.getSnapshotId().isEmpty()
        && request.getBackupType() != BackupType.NODE_INCREMENTAL.get()) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("snapshot_id must be empty when taking snapshots.")
              .asRuntimeException());
      return;
    }

    Optional<ClusterInfoRecord> clusterInfo =
        database.getClusterInfo().selectByClusterId(request.getClusterId());
    if (!clusterInfo.isPresent()) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The specified cluster not found.")
              .asRuntimeException());
      return;
    }

    try {
      JmxManager jmx = getJmx(clusterInfo.get().getTargetIps().get(0), config.getJmxPort());
      if (!areAllNodesAlive(jmx)) {
        throw new RuntimeException("All nodes are not alive.");
      }
    } catch (Exception e) {
      responseObserver.onError(
          Status.UNAVAILABLE
              .withDescription("This method is allowed only when all the nodes are alive.")
              .asRuntimeException());
      return;
    }

    final String snapshotId =
        request.getSnapshotId().isEmpty() ? UUID.randomUUID().toString() : request.getSnapshotId();
    final long incrementalId = request.getSnapshotId().isEmpty() ? 0L : System.currentTimeMillis();
    List<BackupKey> backupKeys =
        getBackupKeys(snapshotId, incrementalId, request.getTargetIpsList(), clusterInfo.get());
    BackupType type = BackupType.getByType(request.getBackupType());
    BackupResponse.Builder builder =
        BackupResponse.newBuilder().setSnapshotId(snapshotId).setIncrementalId(incrementalId);

    try {
      updateBackupStatus(backupKeys, type, OperationStatus.INITIALIZED);
      BackupServiceMaster master =
          new BackupServiceMaster(config, clusterInfo.get(), new RemoteCommandExecutor());
      List<RemoteCommandContext> futures = master.takeBackup(backupKeys, type);
      updateBackupStatus(backupKeys, type, OperationStatus.STARTED);
      futureQueue.addAll(futures);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      builder.setStatus(OperationStatus.FAILED);
      updateBackupStatus(backupKeys, type, OperationStatus.FAILED);
    }

    builder.setStatus(OperationStatus.STARTED);
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    Optional<ClusterInfoRecord> clusterInfo =
        database.getClusterInfo().selectByClusterId(request.getClusterId());
    if (!clusterInfo.isPresent()) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The specified cluster not found.")
              .asRuntimeException());
      return;
    }

    List<BackupKey> backupKeys =
        getBackupKeys(
            request.getSnapshotId(),
            request.getSnapshotOnly() ? 0L : Long.MAX_VALUE,
            request.getTargetIpsList(),
            clusterInfo.get());
    RestoreResponse.Builder builder = RestoreResponse.newBuilder();
    RemoteCommandExecutor executor = new RemoteCommandExecutor();

    // TODO: restore history might be needed in the future

    try {
      RestoreType type = RestoreType.getByType(request.getRestoreType());
      RestoreServiceMaster master = new RestoreServiceMaster(config, clusterInfo.get(), executor);
      List<RemoteCommandContext> futures = master.restoreBackup(backupKeys, type);
      futureQueue.addAll(futures);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      builder.setStatus(OperationStatus.FAILED);
    }

    builder.setStatus(OperationStatus.STARTED);
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  private List<BackupKey> getBackupKeys(
      String snapshotId, long incrementalId, List<String> targets, ClusterInfoRecord record) {
    List<BackupKey> backupKeys = new ArrayList<>();

    if (targets.isEmpty()) {
      targets = record.getTargetIps();
    }

    targets.forEach(
        ip -> {
          BackupKey.Builder keyBuilder =
              BackupKey.newBuilder()
                  .snapshotId(snapshotId)
                  .incrementalId(incrementalId)
                  .clusterId(record.getClusterId())
                  .targetIp(ip);
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

  private void addNodes(
      NodeListingResponse.Builder builder,
      List<String> nodes,
      NodeListingResponse.Entry.NodeStatus status) {
    nodes.forEach(
        ip ->
            builder.addEntries(
                NodeListingResponse.Entry.newBuilder().setIp(ip).setStatus(status).build()));
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
}
