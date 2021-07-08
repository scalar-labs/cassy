package com.scalar.cassy.scheduler;

import com.google.inject.Inject;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
    name = "cluster_snapshot",
    aliases = {"cs"},
    description = "take a cluster-wide snapshot")
class ClusterSnapshot implements Callable<Integer> {
  private static final Logger logger = LoggerFactory.getLogger(ClusterSnapshot.class);
  private final CassyClient client;

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id to backup")
  String clusterId;

  @CommandLine.ParentCommand
  private CassyBackupScheduler scheduler;

  @Inject
  ClusterSnapshot(CassyClient client) {
    this.client = client;
  }

  @Override
  public Integer call() throws Exception {
    logger.info(String.format("Taking cluster wide backup for cluster %s ...", clusterId));
    return client.takeClusterSnapshot(clusterId, scheduler.timeout);
  }
}
