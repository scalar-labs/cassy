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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BackupServerController extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServerController.class.getName());
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
              .withDescription("Please cassandra_host or jmx_port properly.")
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
          .upsert(jmx.getClusterName(), jmx.getLiveNodes(), jmx.getKeyspaces());
      record = database.getClusterInfo().selectByClusterId(jmx.getClusterName());
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL.withCause(e).asRuntimeException());
      return;
    }

    ClusterRegistrationResponse response =
        ClusterRegistrationResponse.newBuilder()
            .setClusterId(record.getClusterId())
            .addAllTargetIps(record.getTargetIps())
            .addAllKeyspaces(record.getKeyspaces())
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

    final String snapshotId =
        request.getSnapshotId().isEmpty() ? UUID.randomUUID().toString() : request.getSnapshotId();
    final long incrementalId = request.getSnapshotId().isEmpty() ? 0L : System.currentTimeMillis();

    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());

    List<BackupKey> backupKeys = new ArrayList<>();
    getTargets(request, jmx)
        .forEach(
            ip ->
                backupKeys.add(
                    BackupKey.newBuilder()
                        .snapshotId(snapshotId)
                        .incrementalId(incrementalId)
                        .clusterId(jmx.getClusterName())
                        .targetIp(ip)
                        .build()));

    BackupType type = BackupType.getByType(request.getBackupType());
    backupKeys.forEach(
        backupKey ->
            database.getBackupHistory().insert(backupKey, type, OperationStatus.INITIALIZED));

    BackupResponse.Builder builder =
        BackupResponse.newBuilder().setSnapshotId(snapshotId).setIncrementalId(incrementalId);

    try {
      BackupServiceMaster master =
          new BackupServiceMaster(config, jmx, new RemoteCommandExecutor());
      List<RemoteCommandContext> futures = master.takeBackup(backupKeys, type);
      backupKeys.forEach(
          backupKey -> database.getBackupHistory().update(backupKey, OperationStatus.STARTED));
      futureQueue.addAll(futures);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      builder.setStatus(OperationStatus.FAILED);
      backupKeys.forEach(
          backupKey -> database.getBackupHistory().update(backupKey, OperationStatus.FAILED));
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    RestoreResponse.Builder builder = RestoreResponse.newBuilder();
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());
    RemoteCommandExecutor executor = new RemoteCommandExecutor();

    List<BackupKey> backupKeys = new ArrayList<>();
    getTargets(request, jmx)
        .forEach(
            ip -> {
              BackupKey.Builder keyBuilder =
                  BackupKey.newBuilder()
                      .snapshotId(request.getSnapshotId())
                      .clusterId(jmx.getClusterName())
                      .targetIp(ip);
              if (request.getSnapshotOnly()) {
                keyBuilder.incrementalId(0L);
              }
              backupKeys.add(keyBuilder.build());
            });

    // TODO: restore history might be needed in the future

    try {
      RestoreType type = RestoreType.getByType(request.getRestoreType());
      RestoreServiceMaster master = new RestoreServiceMaster(config, jmx, executor);
      List<RemoteCommandContext> futures = master.restoreBackup(backupKeys, type);
      futureQueue.addAll(futures);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      builder.setStatus(OperationStatus.FAILED);
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
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

  private List<String> getTargets(BackupRequest request, JmxManager jmx) {
    return request.getTargetIp().isEmpty()
        ? jmx.getLiveNodes()
        : Arrays.asList(request.getTargetIp());
  }

  private List<String> getTargets(RestoreRequest request, JmxManager jmx) {
    return request.getTargetIpsList().isEmpty() ? jmx.getLiveNodes() : request.getTargetIpsList();
  }

  // TODO: There is a duplicate method in AbstractServiceMaster. The other one will be removed in a
  // future PR.
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
