package com.scalar.cassy.scheduler;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.ClusterListingRequest;
import com.scalar.cassy.rpc.OperationStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassyClient {
  private static final Logger logger = LoggerFactory.getLogger(CassyClient.class);
  private final CassyGrpc.CassyBlockingStub blockingStub;

  public CassyClient(CassyGrpc.CassyBlockingStub blockingStub) {
    this.blockingStub = blockingStub;
  }

  public int takeClusterSnapshot(String clusterId, int timeout)
      throws InterruptedException, ExecutionException {
    BackupRequest backupRequest =
        BackupRequest.newBuilder()
            .setClusterId(clusterId)
            .setBackupType(BackupType.CLUSTER_SNAPSHOT.get())
            .build();
    List<Callable<Integer>> tasks = new ArrayList<>();
    tasks.add(
        () -> awaitBackupStatusCompletedOrFailed(blockingStub.takeBackup(backupRequest), timeout));

    return startTasks(tasks);
  }

  public int takeNodeSnapshot(String clusterId, int timeout, List<String> targetIps)
      throws ExecutionException, InterruptedException {
    if (targetIps.isEmpty()) {
      targetIps =
          blockingStub
              .listClusters(ClusterListingRequest.newBuilder().setClusterId(clusterId).build())
              .getEntries(0)
              .getTargetIpsList();
    }

    List<Callable<Integer>> tasks = new ArrayList<>();
    for (String targetIp : targetIps) {
      BackupRequest backupRequest =
          BackupRequest.newBuilder()
              .setClusterId(clusterId)
              .setBackupType(BackupType.NODE_SNAPSHOT.get())
              .addTargetIps(targetIp)
              .build();

      tasks.add(
          () ->
              awaitBackupStatusCompletedOrFailed(blockingStub.takeBackup(backupRequest), timeout));
    }

    return startTasks(tasks);
  }

  public int takeIncrementalBackup(String clusterId, int timeout, List<String> targetIps)
      throws ExecutionException, InterruptedException {
    int code = 0;

    // get cluster target ips if no target ips are set
    if (targetIps.isEmpty()) {
      targetIps =
          blockingStub
              .listClusters(ClusterListingRequest.newBuilder().setClusterId(clusterId).build())
              .getEntries(0)
              .getTargetIpsList();
    }

    List<Callable<Integer>> tasks = new ArrayList<>();
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
      try {
        if (entries.get(0).getStatusValue() != OperationStatus.COMPLETED_VALUE) {
          logger.info(
              String.format(
                  "Could not find completed snapshot for node incremental backup on Target IP %s",
                  targetIp));
          code = 1;
          continue;
        }
      } catch (IndexOutOfBoundsException e) {
        logger.info(
            String.format(
                "No snapshots found for the following parameters:\n\nCluster: %s\nTarget IP: %s",
                clusterId, targetIp));
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

      tasks.add(
          () ->
              awaitBackupStatusCompletedOrFailed(blockingStub.takeBackup(backupRequest), timeout));
    }

    if (code == 1) {
      return code;
    }
    return startTasks(tasks);
  }

  private int awaitBackupStatusCompletedOrFailed(BackupResponse response, int timeout)
      throws InterruptedException {
    BackupListingResponse.Entry entry;
    boolean isNotCompletedOrFailed;
    timeout *= 1000; // convert to millis
    long taskStartedAt = System.currentTimeMillis();
    String targetIps = response.getTargetIps(0);
    if (response.getBackupType() == BackupType.CLUSTER_SNAPSHOT.get()) {
      targetIps = response.getTargetIpsList().toString();
    }
    do {
      if ((System.currentTimeMillis() - taskStartedAt) > timeout) {
        logger.info(
            String.format(
                "\n\nError: The following backup timed out before completion\n\nCluster: %s\nSnapshot: %s\nTarget IP(s): %s\nBackup Type: %s\n",
                response.getClusterId(),
                response.getSnapshotId(),
                targetIps,
                response.getBackupType()));
        return 1;
      }

      entry =
          BackupListingResponse.newBuilder(
                  blockingStub.listBackups(
                      BackupListingRequest.newBuilder()
                          .setClusterId(response.getClusterId())
                          .setSnapshotId(response.getSnapshotId())
                          .setTargetIp(response.getTargetIps(0))
                          .setLimit(1)
                          .build()))
              .build()
              .getEntries(0);

      isNotCompletedOrFailed =
          entry.getStatusValue() != OperationStatus.COMPLETED_VALUE
              && entry.getStatusValue() != OperationStatus.FAILED_VALUE;

      if (isNotCompletedOrFailed) {
        Thread.sleep(2000); // so we aren't spamming Cassy server every millisecond
      }
    } while (isNotCompletedOrFailed);

    logger.info(
        String.format(
            "\n\nThe following backup concluded with status %s\n\nCluster: %s\nSnapshot: %s\nTarget IP(s): %s\nBackup Type: %s\n",
            entry.getStatus().toString(),
            response.getClusterId(),
            response.getSnapshotId(),
            targetIps,
            response.getBackupType()));

    if (entry.getStatusValue() != 3) {
      return 1;
    }
    return 0;
  }

  private int startTasks(List<Callable<Integer>> tasks)
      throws InterruptedException, ExecutionException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    List<Future<Integer>> futures = executorService.invokeAll(tasks);
    for (Future<Integer> future : futures) {
      if (future.get() == 1) {
        return 1;
      }
    }
    return 0;
  }
}
