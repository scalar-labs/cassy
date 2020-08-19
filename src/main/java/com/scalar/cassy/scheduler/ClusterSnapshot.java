package com.scalar.cassy.scheduler;

import com.google.inject.Inject;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "cluster_snapshot", aliases = {"cs"},description = "take a cluster-wide snapshot")
class ClusterSnapshot implements Callable<Integer> {
  private final CassyClient client;

  @CommandLine.ParentCommand
  private CassyBackupScheduler scheduler;

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id to backup")
  String clusterId;

  @Inject
  ClusterSnapshot(CassyClient client) {
    this.client = client;
  }

  @Override
  public Integer call() throws Exception {
    client.takeClusterSnapshot(clusterId, scheduler.timeout);
    return 0;
  }
}
