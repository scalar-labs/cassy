package com.scalar.cassy.scheduler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
    name = "node_snapshot",
    aliases = {"ns"},
    description = "take a node snapshot")
class NodeSnapshot implements Callable<Integer> {
  private static final Logger logger = LoggerFactory.getLogger(NodeSnapshot.class);
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
  NodeSnapshot(CassyClient client) {
    this.client = client;
  }

  @Override
  public Integer call() throws Exception {
    logger.info(String.format("Taking node snapshot for cluster %s ...", clusterId));
    return client.takeNodeSnapshot(clusterId, scheduler.timeout, targetIps);
  }
}
