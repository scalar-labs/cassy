package scheduler;

import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "cluster_snapshot", aliases = {"cs"},description = "take a cluster-wide snapshot")
class ClusterSnapshot implements Callable<Integer> {
  @CommandLine.ParentCommand
  CassyBackupScheduler scheduler;

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
    CassyClient client = new CassyClient(scheduler.timeout);
    return client.takeClusterSnapshot(clusterId);
  }
}
