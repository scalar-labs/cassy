package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.db.ClusterInfoRecord;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupServiceMaster extends AbstractServiceMaster {
  private static final Logger logger = LoggerFactory.getLogger(BackupServiceMaster.class.getName());
  private static final String BACKUP_TYPE_OPTION = "--backup-type=";
  public static final String BACKUP_COMMAND = "cassandra-backup";

  public BackupServiceMaster(
      BackupServerConfig config, ClusterInfoRecord clusterInfo, RemoteCommandExecutor executor) {
    super(config, clusterInfo, executor);
  }

  public List<RemoteCommandContext> takeBackup(List<BackupKey> backupKeys, BackupType type) {
    switch (type) {
      case CLUSTER_SNAPSHOT:
        return takeClusterSnapshots(backupKeys, type);
      case NODE_SNAPSHOT:
      case NODE_INCREMENTAL:
        return takeNodesBackups(backupKeys, type);
      default:
        throw new IllegalArgumentException("Unsupported backup type.");
    }
  }

  private List<RemoteCommandContext> takeClusterSnapshots(
      List<BackupKey> backupKeys, BackupType type) {
    // 1. TODO: stop DLs (coming in a later PR)

    // 2. take snapshots of all the nodes
    takeNodesSnapshots(backupKeys, type);

    // 3. TODO: start DLs (coming in a later PR)

    // 4. copy snapshots in parallel
    return uploadNodesBackups(backupKeys, type);
  }

  private List<RemoteCommandContext> takeNodesBackups(List<BackupKey> backupKeys, BackupType type) {
    // 1. take snapshots of the specified nodes if NODE_SNAPSHOT
    if (type == BackupType.NODE_SNAPSHOT) {
      takeNodesSnapshots(backupKeys, type);
    }

    // 2. copy backups in parallel
    return uploadNodesBackups(backupKeys, type);
  }

  private void takeNodesSnapshots(List<BackupKey> backupKeys, BackupType type) {
    String[] keyspaces = clusterInfo.getKeyspaces().toArray(new String[0]);

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

  private List<RemoteCommandContext> uploadNodesBackups(
      List<BackupKey> backupKeys, BackupType type) {
    List<RemoteCommandContext> futures = new ArrayList<>();

    // Parallel upload for now. It will be adjusted based on workload
    ExecutorService executor = Executors.newCachedThreadPool();
    backupKeys.forEach(
        backupKey ->
            executor.submit(
                () -> {
                  if (type != BackupType.NODE_INCREMENTAL) {
                    removeIncremental(backupKey.getTargetIp());
                  }
                  futures.add(uploadNodeBackups(backupKey, type));
                }));
    awaitTermination(executor, "copyNodesBackups");
    return futures;
  }

  @VisibleForTesting
  void removeIncremental(String ip) {
    // TODO: remove incremental backups (coming in a later PR)
  }

  @VisibleForTesting
  RemoteCommandContext uploadNodeBackups(BackupKey backupKey, BackupType type) {
    List<String> arguments =
        Arrays.asList(
            CLUSTER_ID_OPTION + clusterInfo.getClusterId(),
            SNAPSHOT_ID_OPTION + backupKey.getSnapshotId(),
            TARGET_IP_OPTION + backupKey.getTargetIp(),
            DATA_DIR_OPTION + clusterInfo.getDataDir(),
            STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
            KEYSPACES_OPTION + String.join(",", clusterInfo.getKeyspaces()),
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

    RemoteCommandFuture future = executor.execute(command);
    return new RemoteCommandContext(command, backupKey, future);
  }
}
