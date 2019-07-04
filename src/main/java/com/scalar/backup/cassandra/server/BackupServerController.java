package com.scalar.backup.cassandra.server;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.db.BackupHistory;
import com.scalar.backup.cassandra.db.BackupHistoryRecord;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.BackupListingResponse;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import com.scalar.backup.cassandra.rpc.BackupResponse;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import com.scalar.backup.cassandra.rpc.ClusterListingResponse;
import com.scalar.backup.cassandra.rpc.NodeListingRequest;
import com.scalar.backup.cassandra.rpc.NodeListingResponse;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
import com.scalar.backup.cassandra.rpc.RestoreResponse;
import com.scalar.backup.cassandra.service.BackupKey;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
import com.scalar.backup.cassandra.service.RemoteCommandExecutor;
import com.scalar.backup.cassandra.service.RestoreServiceMaster;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BackupServerController extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServerController.class.getName());
  private final BackupServerConfig config;
  private final BackupHistory history;

  public BackupServerController(BackupServerConfig config, BackupHistory history) {
    this.config = config;
    this.history = history;
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
      records = history.selectRecentSnapshots(request);
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
    backupKeys.forEach(backupKey -> history.insert(backupKey, type, OperationStatus.INITIALIZED));

    BackupResponse.Builder builder =
        BackupResponse.newBuilder().setSnapshotId(snapshotId).setIncrementalId(incrementalId);

    try {
      BackupServiceMaster master =
          new BackupServiceMaster(config, jmx, new RemoteCommandExecutor());
      // TODO: Future will be returned in a later PR
      master.takeBackup(backupKeys, type);
      backupKeys.forEach(backupKey -> history.update(backupKey, OperationStatus.STARTED));
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      builder.setStatus(OperationStatus.FAILED);
      backupKeys.forEach(backupKey -> history.update(backupKey, OperationStatus.FAILED));
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

    try {
      RestoreType type = RestoreType.getByType(request.getRestoreType());
      new RestoreServiceMaster(config, jmx, executor).restoreBackup(backupKeys, type);
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
}
