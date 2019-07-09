package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.remotecommand.RemoteCommand;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandContext;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandExecutor;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandFuture;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreServiceMaster extends AbstractServiceMaster {
  private static final String RESTORE_TYPE_OPTION = "--restore-type=";
  public static final String RESTORE_COMMAND = "cassandra-restore";

  public RestoreServiceMaster(
      BackupServerConfig config, JmxManager jmx, RemoteCommandExecutor executor) {
    super(config, jmx, executor);
  }

  public List<RemoteCommandContext> restoreBackup(List<BackupKey> backupKeys, RestoreType type) {
    if (!areAllNodesAlive()) {
      throw new RemoteExecutionException(
          "This operation is allowed only when all the nodes are alive at the moment.");
    }

    if (type != RestoreType.CLUSTER && type != RestoreType.NODE) {
      throw new IllegalArgumentException("Unsupported restore type.");
    }

    return downloadNodesBackups(backupKeys, type);
  }

  private List<RemoteCommandContext> downloadNodesBackups(
      List<BackupKey> backupKeys, RestoreType type) {
    List<RemoteCommandContext> futures = new ArrayList<>();

    ExecutorService executor = Executors.newCachedThreadPool();
    backupKeys.forEach(
        backupKey ->
            executor.submit(
                () -> {
                  futures.add(downloadNodeBackups(backupKey, type));
                }));
    awaitTermination(executor, "downloadNodesBackups");
    return futures;
  }

  @VisibleForTesting
  RemoteCommandContext downloadNodeBackups(BackupKey backupKey, RestoreType type) {
    List<String> arguments =
        Arrays.asList(
            CLUSTER_ID_OPTION + jmx.getClusterName(),
            BACKUP_ID_OPTION + backupKey.getSnapshotId(),
            TARGET_IP_OPTION + backupKey.getTargetIp(),
            DATA_DIR_OPTION + jmx.getAllDataFileLocations().get(0),
            STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
            KEYSPACES_OPTION + Joiner.on(',').join(jmx.getKeyspaces()),
            RESTORE_TYPE_OPTION + type.get());

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(backupKey.getTargetIp())
            .username(config.getUserName())
            .privateKeyFile(Paths.get(config.getPrivateKeyPath()))
            .name(RESTORE_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + RESTORE_COMMAND)
            .arguments(arguments)
            .build();

    RemoteCommandFuture future = executor.execute(command);
    return new RemoteCommandContext(command, backupKey, future);
  }
}
