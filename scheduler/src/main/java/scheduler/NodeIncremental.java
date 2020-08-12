package scheduler;

import com.google.inject.Inject;
import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "node_incremental", aliases = {"ni"}, description = "take a cluster-wide snapshot")
class NodeIncremental implements Callable<Integer> {
  private final CassyClient client;

  @CommandLine.ParentCommand
  CassyBackupScheduler scheduler;

  @CommandLine.Option(
      names = {"--cluster_id", "-c"},
      required = true,
      description = "cluster id pertaining to the snapshot to backup")
  String clusterId;

  @CommandLine.Option(
      names = {"--target_ips", "-i"},
      description = "optionally specify target ips",
      split = ",")
  String[] targetIps;

  @Inject
  public NodeIncremental(CassyClient client) {
    this.client = client;
  }

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public Integer call() throws Exception {
    return client.takeIncrementalBackup(clusterId, scheduler.timeout, Optional.ofNullable(targetIps));
  }
}
