package com.scalar.backup.cassandra.server;

import com.google.protobuf.Empty;
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
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class BackupServerService extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServerService.class.getName());

  @Override
  public void showClusters(
      com.google.protobuf.Empty request, StreamObserver<ClusterListingResponse> responseObserver) {
    logger.info("showCluster called");
    ClusterListingResponse response =
        ClusterListingResponse.newBuilder().addClusterId("Test Cluster").build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void listNodes(
      NodeListingRequest request, StreamObserver<NodeListingResponse> responseObserver) {
    logger.info("listNodes called with " + request.getClusterId());
    NodeListingResponse response =
        NodeListingResponse.newBuilder()
            .addEntries(NodeListingResponse.Entry.newBuilder().setIp("192.168.1.1").build())
            .build();

    responseObserver.onNext(response);
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
    BackupListingResponse response =
        BackupListingResponse.newBuilder()
            .addEntries(BackupListingResponse.Entry.newBuilder().setBackupId("xxx").build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void takeBackup(BackupRequest request, StreamObserver<BackupResponse> responseObserver) {
    logger.info(
        "takeBackup called with "
            + request.getClusterId()
            + " "
            + request.getTargetIp()
            + " "
            + request.getBackupType());
    BackupResponse response =
        BackupResponse.newBuilder().setBackupId("xxx").setStatus(OperationStatus.STARTED).build();

    responseObserver.onNext(response);
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

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }
}
