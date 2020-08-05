package com.scalar.cassy;

import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import picocli.CommandLine;

@CommandLine.Command(
    name = "node_incremental",
    aliases = {"incremental"},
    description = "take a node incremental backup")
public class NodeIncremental implements Callable<Integer> {
  CassyClient client = new CassyClient();

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(
      names = {"--target_ips", "-t"},
      description = "optionally specify target ips")
  String[] targetIps;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    String snapshotId = "";
    BackupListingResponse backupListingResponse =
        client
            .getBlockingStub()
            .listBackups(BackupListingRequest.newBuilder().setClusterId(clusterId).build());
    for (BackupListingResponse.Entry entry : backupListingResponse.getEntriesList()) {
      if ((entry.getBackupType() == 1 || entry.getBackupType() == 2 ) && entry.getStatusValue() == 3) {
        snapshotId = entry.getSnapshotId();
      }
    }

    if (snapshotId.isEmpty()) return 1;

    // TODO await completed or failed
    return client
        .getBlockingStub()
        .takeBackup(
            BackupRequest.newBuilder()
                .setClusterId(clusterId)
                .setSnapshotId(snapshotId)
                .setBackupType(3)
                .addAllTargetIps(Arrays.stream(targetIps).collect(Collectors.toList()))
                .build())
        .getStatusValue();
  }
}
