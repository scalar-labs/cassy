package com.scalar.cassy;

import com.scalar.cassy.rpc.BackupRequest;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "node_snapshot", description = "take a node snapshot")
public class NodeSnapshot implements Callable<Integer> {
  CassyClient client = new CassyClient();

  @CommandLine.Option(names = {"--cluster_id", "-c"}, required = true, description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(names = {"--target_ips", "-t"}, description = "optionally specify target ips")
  String[] targetIps;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    CassyClient client = new CassyClient();

    BackupRequest backupRequest =
        BackupRequest.newBuilder().setClusterId(clusterId).setBackupType(2).build(); // TODO add targetips
    client.getBlockingStub().takeBackup(backupRequest);

    return null;
  }
}
