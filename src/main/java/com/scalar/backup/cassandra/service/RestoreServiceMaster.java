package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreServiceMaster extends AbstractServiceMaster {
  private final String RESTORE_TYPE_OPTION = "--restore-type=";
  private final String RESTORE_COMMAND = "cassandra-restore";

  public RestoreServiceMaster(
      BackupServerConfig config, JmxManager jmx, RemoteCommandExecutor executor) {
    super(config, jmx, executor);
  }

  public void restoreBackup(RestoreRequest request) {
    if (!areAllNodesAlive()) {
      throw new RemoteExecutionException(
          "This operation is allowed only when all the nodes are alive at the moment.");
    }
    // TODO: status update

    switch (RestoreType.getByType(request.getRestoreType())) {
      case CLUSTER:
        restoreClusterSnapshots(request);
        break;
      case NODE:
        restoreNodesBackups(request);
        break;
      default:
        throw new IllegalArgumentException("Unsupported restore type.");
    }
  }

  private void restoreClusterSnapshots(RestoreRequest request) {
    downloadNodesBackups(jmx.getLiveNodes(), request);
  }

  private void restoreNodesBackups(RestoreRequest request) {
    downloadNodesBackups(request.getTargetIpsList(), request);
  }

  private void downloadNodesBackups(List<String> targets, RestoreRequest request) {
    ExecutorService executor = Executors.newCachedThreadPool();
    targets.forEach(
        ip ->
            executor.submit(
                () -> {
                  downloadNodeBackups(ip, request);
                }));
    awaitTermination(executor, "downloadNodesBackups");
  }

  @VisibleForTesting
  void downloadNodeBackups(String ip, RestoreRequest request) {
    List<String> arguments =
        Arrays.asList(
            CLUSTER_ID_OPTION + jmx.getClusterName(),
            BACKUP_ID_OPTION + request.getBackupId(),
            TARGET_IP_OPTION + ip,
            // TODO: it will be updated in a later PR
            // DATA_DIR_OPTION + jmx.getAllDataFileLocations().get(0),
            DATA_DIR_OPTION + "/tmp/",
            STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
            KEYSPACES_OPTION + Joiner.on(',').join(jmx.getKeyspaces()),
            RESTORE_TYPE_OPTION + request.getRestoreType());

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(ip)
            .username(config.getUserName())
            .privateKeyFile(Paths.get(config.getPrivateKeyPath()))
            .name(RESTORE_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + RESTORE_COMMAND)
            .arguments(arguments)
            .build();

    executor.execute(command);
  }
}
