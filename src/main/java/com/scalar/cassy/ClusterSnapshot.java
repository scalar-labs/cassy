package com.scalar.cassy;

import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine;

@CommandLine.Command(name = "cluster_snapshot", description = "take a cluster-wide snapshot")
public class ClusterSnapshot implements Callable<Integer> {
  CassyClient client = new CassyClient();

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id to backup")
  String clusterId;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    BackupRequest backupRequest =
        BackupRequest.newBuilder().setClusterId(clusterId).setBackupType(1).build();
    BackupResponse backupResponse = client.getBlockingStub().takeBackup(backupRequest);
    ExecutorService executorService = Executors.newCachedThreadPool();
    Callable<Object> task = () -> awaitBackupStatusCompletedOrFailed(backupResponse);

    Future<Object> future = executorService.submit(task);
    future.get(20, TimeUnit.SECONDS); // wait a maximum of 20 seconds

    return awaitBackupStatusCompletedOrFailed(backupResponse).getEntries(0).getStatusValue();
  }

  private BackupListingResponse awaitBackupStatusCompletedOrFailed(BackupResponse response) throws InterruptedException {
    BackupListingResponse backupListingResponse;
    do {
      backupListingResponse = BackupListingResponse.newBuilder(
          client
              .getBlockingStub()
              .listBackups(
                  BackupListingRequest.newBuilder()
                      .setClusterId(clusterId)
                      .setSnapshotId(response.getSnapshotId())
                      .build())).build();
      Thread.sleep(2000); // so we aren't spamming Cassy server every millisecond
    } while (backupListingResponse.getEntries(0).getStatusValue() != 3
        && backupListingResponse.getEntries(0).getStatusValue() != 4);

    return backupListingResponse;
  }
}
