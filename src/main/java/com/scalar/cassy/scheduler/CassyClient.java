package com.scalar.cassy.scheduler;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.BackupException;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.ClusterListingRequest;
import com.scalar.cassy.rpc.ClusterListingResponse;
import com.scalar.cassy.rpc.OperationStatus;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class CassyClient {
  private final CassyGrpc.CassyBlockingStub blockingStub;

  public CassyClient() {
    this(
        ManagedChannelBuilder.forAddress(
                System.getenv().getOrDefault("CASSY_SCHEDULER_HOST", "localhost"),
                Integer.parseInt(System.getenv().getOrDefault("CASSY_SCHEDULER_PORT", "20051")))
            .usePlaintext());
  }

  public CassyClient(ManagedChannelBuilder<?> channelBuilder) {
    Channel channel = channelBuilder.build();
    blockingStub = CassyGrpc.newBlockingStub(channel);
  }

  public void takeClusterSnapshot(String clusterId, int timeout) throws Exception {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.CLUSTER_SNAPSHOT.get())
            .build();
    BackupResponse backupResponse = blockingStub.takeBackup(backupRequest);
    startTask(backupResponse, timeout);
  }

  public void takeNodeSnapshot(String clusterId, int timeout, List<String> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.NODE_SNAPSHOT.get())
            .build();
    if (!targetIps.isEmpty()) {
      backupRequest =
          BackupRequest.newBuilder(backupRequest)
              .addAllTargetIps(targetIps)
              .build();
    }

    startTask(blockingStub.takeBackup(backupRequest), timeout);
  }

  public void takeIncrementalBackup(String clusterId, int timeout, List<String> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {

    // get cluster target ips if no target ips are set
    if (targetIps.isEmpty()) {
      targetIps =
          blockingStub.listClusters(
              ClusterListingRequest.newBuilder().setClusterId(clusterId).build()).getEntries(0).getTargetIpsList();
    }

    // for each target_ip, get a backup listing response
    for (String targetIp : targetIps) {
      BackupListingResponse backupListingResponse =
          blockingStub.listBackups(
              BackupListingRequest.newBuilder()
                  .setClusterId(clusterId)
                  .setTargetIp(targetIp)
                  .build());
      List<BackupListingResponse.Entry> entries =
          backupListingResponse.getEntriesList().stream()
              .filter(e -> e.getBackupType() != BackupType.NODE_INCREMENTAL.get())
              .collect(Collectors.toList());
      // get the most recent snapshot
      if (entries.get(0).getStatusValue() != OperationStatus.COMPLETED_VALUE) {
        // if snapshot is not COMPLETED, throw an exception
        throw new BackupException(
            "Most recent snapshot is not completed. Unable to take incremental backup.");
      }
      // if snapshot is COMPLETED, take an incremental backup with target_ip
      BackupRequest backupRequest =
          BackupRequest.newBuilder()
              .setClusterId(clusterId)
              .addTargetIps(targetIp)
              .setSnapshotId(entries.get(0).getSnapshotId())
              .setBackupType(BackupType.NODE_INCREMENTAL.get())
          .build();

      startTask(blockingStub.takeBackup(backupRequest), timeout);
    }
  }

  private BackupListingResponse awaitBackupStatusCompletedOrFailed(BackupResponse response)
      throws InterruptedException {
    BackupListingResponse backupListingResponse;
    do {
      backupListingResponse =
          BackupListingResponse.newBuilder(
                  blockingStub.listBackups(
                      BackupListingRequest.newBuilder()
                          .setClusterId(response.getClusterId())
                          .setSnapshotId(response.getSnapshotId())
                          .setLimit(1)
                          .build()))
              .build();
      Thread.sleep(2000); // so we aren't spamming Cassy server every millisecond
    } while (backupListingResponse.getEntries(0).getStatusValue() != OperationStatus.COMPLETED_VALUE
        && backupListingResponse.getEntries(0).getStatusValue() != OperationStatus.FAILED_VALUE);
    if (backupListingResponse.getEntries(0).getStatusValue() == OperationStatus.FAILED_VALUE) {
      throw new BackupException(
          String.format(
              "Failed to create backup for cluster '%s' with snapshot_id '%s'",
              response.getClusterId(), response.getSnapshotId()));
    }

    return backupListingResponse;
  }

  private void startTask(BackupResponse response, int timeout)
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Callable<BackupListingResponse> task = () -> awaitBackupStatusCompletedOrFailed(response);
    Future<BackupListingResponse> future = executorService.submit(task);
    future.get(timeout, TimeUnit.SECONDS);
  }
}
