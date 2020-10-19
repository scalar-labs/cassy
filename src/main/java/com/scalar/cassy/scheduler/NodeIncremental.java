package com.scalar.cassy.scheduler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
    name = "node_incremental",
    aliases = {"ni"},
    description = "take a cluster-wide snapshot")
class NodeIncremental implements Callable<Integer> {
  private static final Logger logger = LoggerFactory.getLogger(NodeIncremental.class);
  private final CassyClient client;

  @CommandLine.ParentCommand CassyBackupScheduler scheduler;

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(
      names = {"--target_ips", "-i"},
      description = "optionally specify target ips",
      split = ",")
  List<String> targetIps = new ArrayList<>();

  @Inject
  public NodeIncremental(CassyClient client) {
    this.client = client;
  }

  @Override
  public Integer call() throws Exception {
    logger.info(String.format("Taking node incremental backup for cluster %s ...", clusterId));
    return client.takeIncrementalBackup(clusterId, scheduler.timeout, targetIps);
  }
}
