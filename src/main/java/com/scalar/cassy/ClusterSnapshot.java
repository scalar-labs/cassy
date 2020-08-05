package com.scalar.cassy;

import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import java.util.concurrent.Callable;
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

    return awaitBackupStatusCompletedOrFailed(backupResponse).getEntries(0).getStatusValue();
  }

  private BackupListingResponse awaitBackupStatusCompletedOrFailed(BackupResponse response) {
    // TODO finish this method because it doesn't actually wait
    BackupListingResponse backupListingResponse;
    backupListingResponse =
        client
            .getBlockingStub()
            .listBackups(
                BackupListingRequest.newBuilder()
                    .setClusterId(clusterId)
                    .setSnapshotId(response.getSnapshotId())
                    .build());

    return backupListingResponse;
  }
}
