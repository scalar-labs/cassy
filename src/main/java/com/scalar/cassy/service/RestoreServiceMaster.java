package com.scalar.cassy.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.config.RestoreType;
import com.scalar.cassy.db.ClusterInfoRecord;
import com.scalar.cassy.remotecommand.RemoteCommand;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import com.scalar.cassy.remotecommand.RemoteCommandFuture;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreServiceMaster extends AbstractServiceMaster {
  public static final String RESTORE_COMMAND = "cassy-restore";
  private static final String RESTORE_TYPE_OPTION = "--restore-type=";
  private static final String SNAPSHOT_ONLY_OPTION = "--snapshot-only";

  public RestoreServiceMaster(
      CassyServerConfig config, ClusterInfoRecord clusterInfo, RemoteCommandExecutor executor) {
    super(config, clusterInfo, executor);
  }

  public List<RemoteCommandContext> restoreBackup(List<BackupKey> backupKeys, RestoreType type) {
    return restoreBackup(backupKeys, type, false);
  }

  public List<RemoteCommandContext> restoreBackup(
      List<BackupKey> backupKeys, RestoreType type, boolean snapshotOnly) {
    if (type != RestoreType.CLUSTER && type != RestoreType.NODE) {
      throw new IllegalArgumentException("Unsupported restore type.");
    }

    return downloadNodesBackups(backupKeys, type, snapshotOnly);
  }

  private List<RemoteCommandContext> downloadNodesBackups(
      List<BackupKey> backupKeys, RestoreType type, boolean snapshotOnly) {
    List<RemoteCommandContext> futures = new ArrayList<>();

    ExecutorService executor = Executors.newCachedThreadPool();
    backupKeys.forEach(
        backupKey ->
            executor.submit(
                () -> {
                  futures.add(downloadNodeBackups(backupKey, type, snapshotOnly));
                }));
    awaitTermination(executor, "downloadNodesBackups");
    return futures;
  }

  @VisibleForTesting
  RemoteCommandContext downloadNodeBackups(
      BackupKey backupKey, RestoreType type, boolean snapshotOnly) {
    List<String> arguments = new ArrayList<>();
    arguments.add(CLUSTER_ID_OPTION + backupKey.getClusterId());
    arguments.add(SNAPSHOT_ID_OPTION + backupKey.getSnapshotId());
    arguments.add(TARGET_IP_OPTION + backupKey.getTargetIp());
    arguments.add(DATA_DIR_OPTION + clusterInfo.getDataDir());
    arguments.add(STORE_BASE_URI_OPTION + config.getStorageBaseUri());
    arguments.add(STORE_TYPE_OPTION + config.getStorageType());
    arguments.add(KEYSPACES_OPTION + String.join(",", clusterInfo.getKeyspaces()));
    arguments.add(RESTORE_TYPE_OPTION + type.get());
    if (snapshotOnly) {
      arguments.add(SNAPSHOT_ONLY_OPTION);
    }

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(backupKey.getTargetIp())
            .username(config.getSshUser())
            .privateKeyFile(Paths.get(config.getSshPrivateKeyPath()))
            .name(RESTORE_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + RESTORE_COMMAND)
            .arguments(arguments)
            .build();

    RemoteCommandFuture future = executor.execute(command);
    return new RemoteCommandContext(command, backupKey, future);
  }
}
