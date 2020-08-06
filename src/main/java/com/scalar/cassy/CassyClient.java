package com.scalar.cassy;

import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
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
    this(ManagedChannelBuilder.forAddress("localhost", 20051).usePlaintext());
  }

  public CassyClient(ManagedChannelBuilder<?> channelBuilder) {
    Channel channel = channelBuilder.build();
    blockingStub = CassyGrpc.newBlockingStub(channel);
  }

  public CassyGrpc.CassyBlockingStub getBlockingStub() {
    return blockingStub;
  }

  private int startTask(BackupResponse response)
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Callable<BackupListingResponse> task = () -> awaitBackupStatusCompletedOrFailed(response);
    Future<BackupListingResponse> future = executorService.submit(task);
    return future
        .get(20, TimeUnit.SECONDS)
        .getEntries(0)
        .getStatusValue(); // wait a maximum of 20 seconds
  }

  public int takeClusterSnapshot(String clusterId) throws Exception {
    BackupRequest backupRequest =
        BackupRequest.newBuilder().setClusterId(clusterId).setBackupType(1).build();
    BackupResponse backupResponse = blockingStub.takeBackup(backupRequest);
    return startTask(backupResponse);
  }

  public int takeIncrementalBackup(String clusterId, Optional<String[]> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    String snapshotId = "";
    BackupListingResponse backupListingResponse =
        blockingStub.listBackups(BackupListingRequest.newBuilder().setClusterId(clusterId).build());
    for (BackupListingResponse.Entry entry : backupListingResponse.getEntriesList()) {
      if ((entry.getBackupType() == 1 || entry.getBackupType() == 2)
          && entry.getStatusValue() == 3) {
        snapshotId = entry.getSnapshotId();
      }
    }

    if (snapshotId.isEmpty()) return 1;

    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setSnapshotId(snapshotId)
            .setBackupType(3)
            .build();

    if (targetIps.isPresent()) {
      backupRequest =
          BackupRequest.newBuilder(backupRequest)
              .addAllTargetIps(Arrays.stream(targetIps.get()).collect(Collectors.toList()))
              .build();
    }

    return startTask(blockingStub.takeBackup(backupRequest));
  }

  public int takeNodeSnapshot(String clusterId, Optional<String[]> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    BackupRequest backupRequest =
        BackupRequest.newBuilder().setClusterId(clusterId).setBackupType(2).build();
    if (targetIps.isPresent()) {
      backupRequest =
          BackupRequest.newBuilder(backupRequest)
              .addAllTargetIps(Arrays.stream(targetIps.get()).collect(Collectors.toList()))
              .build();
    }

    return startTask(blockingStub.takeBackup(backupRequest));
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
    } while (backupListingResponse.getEntries(0).getStatusValue() != 3
        && backupListingResponse.getEntries(0).getStatusValue() != 4);

    return backupListingResponse;
  }
}
