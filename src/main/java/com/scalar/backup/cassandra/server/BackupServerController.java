package com.scalar.backup.cassandra.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Empty;
import com.palantir.giraffe.ssh.SshCredential;
import com.scalar.backup.cassandra.config.BackupServerConfig;
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
import com.scalar.backup.cassandra.rpc.StatusUpdateRequest;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class BackupServerController extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServerController.class.getName());
  private final BackupServerConfig config;
  private final SshCredential credential;

  public BackupServerController(BackupServerConfig config, SshCredential credential) {
    this.config = config;
    this.credential = credential;
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
    logger.info(
        "listBackups called with "
            + request.getClusterId()
            + " "
            + request.getTargetIp()
            + " "
            + request.getN());

    // TODO: implementation is coming in a later PR
    BackupListingResponse response =
        BackupListingResponse.newBuilder()
            .addEntries(
                BackupListingResponse.Entry.newBuilder()
                    .setBackupId("xxx")
                    .setClusterId(request.getClusterId())
                    .setTargetIp(request.getTargetIp())
                    .build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void takeBackup(BackupRequest request, StreamObserver<BackupResponse> responseObserver) {
    String backupId = Long.toString(System.currentTimeMillis()) + "-" + UUID.randomUUID();
    BackupResponse.Builder builder =
        BackupResponse.newBuilder().setBackupId(backupId).setStatus(OperationStatus.SUCCEEDED);
    JmxManager jmx = getJmx(config.getCassandraHost(), config.getJmxPort());

    try {
      new BackupServiceMaster(config, jmx, credential).take(backupId, request);
    } catch (Exception e) {
      builder.setStatus(OperationStatus.FAILED);
      builder.setMessage(e.getMessage());
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void restoreBackup(
      RestoreRequest request, StreamObserver<RestoreResponse> responseObserver) {
    logger.info(
        "restoreBackup called with "
            + request.getClusterId()
            + " "
            + request.getBackupId()
            + " "
            + request.getTargetIpsList()
            + " "
            + request.getRestoreType());
    RestoreResponse response =
        RestoreResponse.newBuilder().setStatus(OperationStatus.STARTED).build();

    // TODO: implementation is coming in a later PR
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  public void updateStatus(
      StatusUpdateRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
    logger.info(
        "updateStatus called with "
            + request.getClusterId()
            + " "
            + request.getBackupId()
            + " "
            + request.getTargetIp()
            + " "
            + request.getStatus());

    // TODO: implementation is coming in a later PR
    responseObserver.onNext(Empty.newBuilder().build());
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
