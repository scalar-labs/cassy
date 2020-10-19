package com.scalar.cassy.scheduler;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.BackupException;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.ClusterListingRequest;
import com.scalar.cassy.rpc.OperationStatus;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassyClient {
  private static final Logger logger = LoggerFactory.getLogger(CassyClient.class);
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

  public int takeClusterSnapshot(String clusterId, int timeout) throws Exception {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.CLUSTER_SNAPSHOT.get())
            .build();
    if (startTask(blockingStub.takeBackup(backupRequest), timeout) != 3) {
      return 1;
    }
    return 0;
  }

  public int takeNodeSnapshot(String clusterId, int timeout, List<String> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.NODE_SNAPSHOT.get())
            .build();
    if (!targetIps.isEmpty()) {
      backupRequest = BackupRequest.newBuilder(backupRequest).addAllTargetIps(targetIps).build();
    }

    if (startTask(blockingStub.takeBackup(backupRequest), timeout) != 3) {
      return 1;
    }
    return 0;
  }

  public int takeIncrementalBackup(String clusterId, int timeout, List<String> targetIps)
      throws InterruptedException, TimeoutException, ExecutionException {
    int code = 0;

    // get cluster target ips if no target ips are set
    if (targetIps.isEmpty()) {
      targetIps =
          blockingStub
              .listClusters(ClusterListingRequest.newBuilder().setClusterId(clusterId).build())
              .getEntries(0)
              .getTargetIpsList();
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
        logger.error(String.format("Could not find completed snapshot for node incremental backup on Target IP %s", targetIp));
        code = 1;
        continue;
      }
      // if snapshot is COMPLETED, take an incremental backup with target_ip
      BackupRequest backupRequest =
          BackupRequest.newBuilder()
              .setClusterId(clusterId)
              .addTargetIps(targetIp)
              .setSnapshotId(entries.get(0).getSnapshotId())
              .setBackupType(BackupType.NODE_INCREMENTAL.get())
              .build();

      if (startTask(blockingStub.takeBackup(backupRequest), timeout) != 3) {
        code = 1;
      }
    }
    return code;
  }

  private int awaitBackupStatusCompletedOrFailed(BackupResponse response)
      throws InterruptedException {
    BackupListingResponse backupListingResponse;
    boolean isNotCompletedOrFailed;
    do {
      backupListingResponse =
          BackupListingResponse.newBuilder(
                  blockingStub.listBackups(
                      BackupListingRequest.newBuilder()
                          .setClusterId(response.getClusterId())
                          .setSnapshotId(response.getSnapshotId())
                          .setTargetIp(response.getTargetIps(0))
                          .setLimit(1)
                          .build()))
              .build();

      isNotCompletedOrFailed = backupListingResponse.getEntries(0).getStatusValue() != OperationStatus.COMPLETED_VALUE
          && backupListingResponse.getEntries(0).getStatusValue() != OperationStatus.FAILED_VALUE;

      if (isNotCompletedOrFailed) {
        Thread.sleep(10000); // so we aren't spamming Cassy server every millisecond
      }
    } while (isNotCompletedOrFailed);

    OperationStatus status = backupListingResponse.getEntries(0).getStatus();
    logger.info(String.format("\n\nThe following backup concluded with status %s\n\nCluster: %s\nSnapshot: %s\nTarget IP: %s\nBackup Type: %s",
        status.toString(),
        response.getClusterId(),
        response.getSnapshotId(),
        response.getTargetIps(0),
        response.getBackupType()
    ));
    return status.getNumber();
  }

  private int startTask(BackupResponse response, int timeout)
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Callable<Integer> task = () -> awaitBackupStatusCompletedOrFailed(response);
    Future<Integer> future = executorService.submit(task);
    return future.get(timeout, TimeUnit.SECONDS);
  }
}
