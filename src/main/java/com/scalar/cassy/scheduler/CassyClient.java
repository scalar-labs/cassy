package com.scalar.cassy.scheduler;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.OperationStatus;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.Arrays;
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
            Integer.parseInt(System.getenv().getOrDefault("CASSY_SCHEDULER_PORT", "20051"))).usePlaintext()
    );
  }

  public CassyClient(ManagedChannelBuilder<?> channelBuilder) {
    Channel channel = channelBuilder.build();
    blockingStub = CassyGrpc.newBlockingStub(channel);
  }

  public int takeClusterSnapshot(String clusterId, int timeout) throws Exception {
    BackupRequest backupRequest =
        BackupRequest.newBuilder().setClusterId(clusterId).setBackupType(BackupType.CLUSTER_SNAPSHOT.get()).build();
    BackupResponse backupResponse = blockingStub.takeBackup(backupRequest);
    return startTask(backupResponse, timeout);
  }

  public int takeNodeSnapshot(String clusterId, int timeout, Optional<String[]> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.NODE_SNAPSHOT.get())
            .build();
    if (targetIps.isPresent()) {
      backupRequest =
          BackupRequest.newBuilder(backupRequest)
              .addAllTargetIps(Arrays.stream(targetIps.get()).collect(Collectors.toList()))
              .build();
    }

    return startTask(blockingStub.takeBackup(backupRequest), timeout);
  }

  public int takeIncrementalBackup(String clusterId, int timeout, Optional<String[]> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    String snapshotId = "";
    BackupListingResponse backupListingResponse =
        blockingStub.listBackups(BackupListingRequest.newBuilder().setClusterId(clusterId).build());
    for (BackupListingResponse.Entry entry : backupListingResponse.getEntriesList()) {
      if ((entry.getBackupType() == BackupType.CLUSTER_SNAPSHOT.get() || entry.getBackupType() == BackupType.NODE_SNAPSHOT.get())
          && entry.getStatusValue() == OperationStatus.COMPLETED_VALUE) {
        snapshotId = entry.getSnapshotId();
      }
    }

    if (snapshotId.isEmpty()) return 1;

    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setSnapshotId(snapshotId)
            .setBackupType(BackupType.NODE_INCREMENTAL.get())
            .build();

    if (targetIps.isPresent()) {
      backupRequest =
          BackupRequest.newBuilder(backupRequest)
              .addAllTargetIps(Arrays.stream(targetIps.get()).collect(Collectors.toList()))
              .build();
    }

    return startTask(blockingStub.takeBackup(backupRequest), timeout);
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

    return backupListingResponse;
  }

  private int startTask(BackupResponse response, int timeout)
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Callable<BackupListingResponse> task = () -> awaitBackupStatusCompletedOrFailed(response);
    Future<BackupListingResponse> future = executorService.submit(task);
    return future
        .get(timeout, TimeUnit.SECONDS)
        .getEntries(0)
        .getStatusValue();
  }
}
