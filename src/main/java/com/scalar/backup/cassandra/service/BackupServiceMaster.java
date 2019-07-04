package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupServiceMaster extends AbstractServiceMaster {
  private static final Logger logger = LoggerFactory.getLogger(BackupServiceMaster.class.getName());
  private final String BACKUP_TYPE_OPTION = "--backup-type=";
  private final String BACKUP_COMMAND = "cassandra-backup";

  public BackupServiceMaster(
      BackupServerConfig config, JmxManager jmx, RemoteCommandExecutor executor) {
    super(config, jmx, executor);
  }

  public void takeBackup(List<BackupKey> backupKeys, BackupType type) {
    if (!areAllNodesAlive()) {
      throw new RemoteExecutionException(
          "This operation is allowed only when all the nodes are alive at the moment.");
    }

    switch (type) {
      case CLUSTER_SNAPSHOT:
        takeClusterSnapshots(backupKeys, type);
        break;
      case NODE_SNAPSHOT:
      case NODE_INCREMENTAL:
        takeNodesBackups(backupKeys, type);
        break;
      default:
        throw new IllegalArgumentException("Unsupported backup type.");
    }
  }

  private void takeClusterSnapshots(List<BackupKey> backupKeys, BackupType type) {
    // 1. TODO: stop DLs (coming in a later PR)

    // 2. take snapshots of all the nodes
    takeNodesSnapshots(backupKeys, type);

    // 3. TODO: start DLs (coming in a later PR)

    // 4. copy snapshots in parallel
    uploadNodesBackups(backupKeys, type);
  }

  private void takeNodesBackups(List<BackupKey> backupKeys, BackupType type) {
    // 1. take snapshots of the specified nodes if NODE_SNAPSHOT
    if (type == BackupType.NODE_SNAPSHOT) {
      takeNodesSnapshots(backupKeys, type);
    }

    // 2. copy backups in parallel
    uploadNodesBackups(backupKeys, type);
  }

  private void takeNodesSnapshots(List<BackupKey> backupKeys, BackupType type) {
    String[] keyspaces = jmx.getKeyspaces().toArray(new String[0]);

    ExecutorService executor = Executors.newCachedThreadPool();
    backupKeys.forEach(
        backupKey ->
            executor.submit(
                () -> {
                  JmxManager eachJmx = getJmx(backupKey.getTargetIp(), config.getJmxPort());
                  eachJmx.clearSnapshot(null, keyspaces);
                  eachJmx.takeSnapshot(backupKey.getSnapshotId(), keyspaces);
                }));
    awaitTermination(executor, "takeNodesSnapshots");
  }

  private void uploadNodesBackups(List<BackupKey> backupKeys, BackupType type) {
    // Parallel upload for now. It will be adjusted based on workload
    ExecutorService executor = Executors.newCachedThreadPool();
    backupKeys.forEach(
        backupKey ->
            executor.submit(
                () -> {
                  if (type != BackupType.NODE_INCREMENTAL) {
                    removeIncremental(backupKey.getTargetIp());
                  }
                  uploadNodeBackups(backupKey, type);
                }));
    awaitTermination(executor, "copyNodesBackups");
  }

  @VisibleForTesting
  void removeIncremental(String ip) {
    // TODO: remove incremental backups (coming in a later PR)
  }

  @VisibleForTesting
  void uploadNodeBackups(BackupKey backupKey, BackupType type) {
    List<String> arguments =
        Arrays.asList(
            CLUSTER_ID_OPTION + jmx.getClusterName(),
            BACKUP_ID_OPTION + backupKey.getSnapshotId(),
            TARGET_IP_OPTION + backupKey.getTargetIp(),
            DATA_DIR_OPTION + jmx.getAllDataFileLocations().get(0),
            STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
            KEYSPACES_OPTION + Joiner.on(',').join(jmx.getKeyspaces()),
            BACKUP_TYPE_OPTION + type.get());

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(backupKey.getTargetIp())
            .username(config.getUserName())
            .privateKeyFile(Paths.get(config.getPrivateKeyPath()))
            .name(BACKUP_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + BACKUP_COMMAND)
            .arguments(arguments)
            .build();

    executor.execute(command);
  }
}
