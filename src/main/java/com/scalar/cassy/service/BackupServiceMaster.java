package com.scalar.cassy.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.db.ClusterInfoRecord;
import com.scalar.cassy.jmx.JmxManager;
import com.scalar.cassy.remotecommand.RemoteCommand;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import com.scalar.cassy.remotecommand.RemoteCommandFuture;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupServiceMaster extends AbstractServiceMaster {
  public static final String BACKUP_COMMAND = "cassy-backup";
  private static final Logger logger = LoggerFactory.getLogger(BackupServiceMaster.class);
  private static final String BACKUP_TYPE_OPTION = "--backup-type=";
  private ApplicationPauser pauser;

  public BackupServiceMaster(
      CassyServerConfig config,
      ClusterInfoRecord clusterInfo,
      RemoteCommandExecutor executor,
      ApplicationPauser pauser) {
    super(config, clusterInfo, executor);
    this.pauser = pauser;
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
    // 1. pause C* applications
    pauser.pause();

    // 2. take snapshots of all the nodes in a C* cluster
    takeNodesSnapshots(backupKeys);

    // 3. unpause C* applications
    pauser.unpause();

    // 4. copy snapshots in parallel
    return uploadNodesBackups(backupKeys, type);
  }

  private List<RemoteCommandContext> takeNodesBackups(List<BackupKey> backupKeys, BackupType type) {
    // 1. take snapshots of the specified nodes if NODE_SNAPSHOT
    if (type == BackupType.NODE_SNAPSHOT) {
      takeNodesSnapshots(backupKeys);
    }

    // 2. copy backups in parallel
    return uploadNodesBackups(backupKeys, type);
  }

  private void takeNodesSnapshots(List<BackupKey> backupKeys) {
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
        backupKey -> executor.submit(() -> futures.add(uploadNodeBackups(backupKey, type))));
    awaitTermination(executor, "copyNodesBackups");
    return futures;
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
            STORE_TYPE_OPTION + config.getStorageType(),
            KEYSPACES_OPTION + String.join(",", clusterInfo.getKeyspaces()),
            BACKUP_TYPE_OPTION + type.get());

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(backupKey.getTargetIp())
            .username(config.getSshUser())
            .privateKeyFile(Paths.get(config.getSshPrivateKeyPath()))
            .name(BACKUP_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + BACKUP_COMMAND)
            .arguments(arguments)
            .build();

    RemoteCommandFuture future = executor.execute(command);
    return new RemoteCommandContext(command, backupKey, future);
  }
}
