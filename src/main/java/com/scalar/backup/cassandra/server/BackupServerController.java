package com.scalar.backup.cassandra.server;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.db.BackupHistory;
import com.scalar.backup.cassandra.db.BackupHistoryRecord;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.BackupListingResponse;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import com.scalar.backup.cassandra.rpc.BackupResponse;
import com.scalar.backup.cassandra.rpc.BackupStatus;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import com.scalar.backup.cassandra.rpc.ClusterListingResponse;
import com.scalar.backup.cassandra.rpc.NodeListingRequest;
import com.scalar.backup.cassandra.rpc.NodeListingResponse;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
import com.scalar.backup.cassandra.rpc.RestoreResponse;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
import com.scalar.backup.cassandra.service.RemoteCommandExecutor;
import com.scalar.backup.cassandra.service.RestoreServiceMaster;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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
                  .setBackupId(r.getBackupId())
                  .setClusterId(r.getClusterId())
                  .setTargetIp(r.getTargetIp())
                  .setBackupType(r.getBackupType().get())
                  .setStatus(BackupStatus.forNumber(r.getStatus().getNumber()))
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
              .withDescription("snapshot must be empty when taking snapshots.")
              .asRuntimeException());
      return;
    }

    String snapshotId = request.getSnapshotId();
    if (request.getSnapshotId().isEmpty()) {
      snapshotId = UUID.randomUUID().toString();
    }
    long backupId = System.currentTimeMillis();

    // TODO: it will be removed when the backup processing is asynchronous
    history.insert(request, snapshotId, backupId, BackupStatus.STARTED);

    BackupResponse.Builder builder = BackupResponse.newBuilder().setBackupId(backupId);
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());
    RemoteCommandExecutor executor = new RemoteCommandExecutor();

    try {
      new BackupServiceMaster(config, jmx, executor).takeBackup(snapshotId, request);

      // TODO: it will be removed when the backup processing is asynchronous
      history.update(request, snapshotId, backupId, BackupStatus.COMPLETED);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
      // TODO: it will be removed when the backup processing is asynchronous
      history.update(request, snapshotId, backupId, BackupStatus.FAILED);
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {

    // TODO: restoration status might be added in a later PR

    RestoreResponse.Builder builder = RestoreResponse.newBuilder();
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());
    RemoteCommandExecutor executor = new RemoteCommandExecutor();

    try {
      new RestoreServiceMaster(config, jmx, executor).restoreBackup(request);
    } catch (Exception e) {
      builder.setMessage(e.getMessage());
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
}
