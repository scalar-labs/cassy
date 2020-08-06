package com.scalar.cassy;

import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
    name = "cassy-schedule",
    mixinStandardHelpOptions = true,
    description = "calls Cassy's backup command",
    subcommands = {ClusterSnapshot.class, NodeSnapshot.class, NodeIncremental.class})
public class CassyBackupScheduler implements Runnable {

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  public static void main(String[] args) {
    System.exit(new CommandLine(new CassyBackupScheduler()).execute(args));
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread.
   *
   * <p>The general contract of the method <code>run</code> is that it may take any action
   * whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    spec.commandLine().usage(System.err);
  }
}

@CommandLine.Command(name = "cluster_snapshot", aliases = {"cs"},description = "take a cluster-wide snapshot")
class ClusterSnapshot implements Callable<Integer> {
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
    return client.takeClusterSnapshot(clusterId);
  }
}

@CommandLine.Command(name = "node_snapshot", aliases = {"ns"}, description = "take a node snapshot")
class NodeSnapshot implements Callable<Integer> {
  CassyClient client = new CassyClient();

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(
      names = {"--target_ips", "-t"},
      description = "optionally specify target ips",
      split = ",")
  String[] targetIps;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    return client.takeNodeSnapshot(clusterId, Optional.ofNullable(targetIps));
  }
}

@CommandLine.Command(name = "node_incremental", aliases = {"ni"}, description = "take a cluster-wide snapshot")
class NodeIncremental implements Callable<Integer> {
  CassyClient client = new CassyClient();

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(
      names = {"--target_ips", "-t"},
      description = "optionally specify target ips",
      split = ",")
  String[] targetIps;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    return client.takeIncrementalBackup(clusterId, Optional.ofNullable(targetIps));
  }
}
