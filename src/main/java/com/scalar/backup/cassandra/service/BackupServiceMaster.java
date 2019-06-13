package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.BackupRequest;
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

  public void takeBackup(String backupId, BackupRequest request) {
    if (!areAllNodesAlive()) {
      throw new RemoteExecutionException(
          "This operation is allowed only when all the nodes are alive at the moment.");
    }
    // TODO: status update

    switch (BackupType.getByType(request.getBackupType())) {
      case CLUSTER_SNAPSHOT:
        takeClusterSnapshots(backupId, request);
        break;
      case NODE_SNAPSHOT:
      case NODE_INCREMENTAL:
        takeNodesBackups(backupId, request);
        break;
      default:
        throw new IllegalArgumentException("Unsupported backup type.");
    }
  }

  private void takeClusterSnapshots(String backupId, BackupRequest request) {
    // 1. TODO: stop DLs (coming in a later PR)

    // 2. take snapshots of all the nodes
    takeNodesSnapshots(backupId, jmx.getLiveNodes(), request);

    // 3. TODO: start DLs (coming in a later PR)

    // 4. copy snapshots in parallel
    uploadNodesBackups(backupId, jmx.getLiveNodes(), request);
  }

  private void takeNodesBackups(String backupId, BackupRequest request) {
    List<String> targets = jmx.getLiveNodes();
    if (!request.getTargetIp().isEmpty()) {
      targets = Arrays.asList(request.getTargetIp());
    }

    // 1. take snapshots of the specified nodes if NODE_SNAPSHOT
    if (request.getBackupType() == BackupType.NODE_SNAPSHOT.get()) {
      takeNodesSnapshots(backupId, targets, request);
    }

    // 2. copy backups in parallel
    uploadNodesBackups(backupId, targets, request);
  }

  private void takeNodesSnapshots(String backupId, List<String> targets, BackupRequest request) {
    String[] keyspaces = getTargetKeyspaces(request).toArray(new String[0]);

    ExecutorService executor = Executors.newCachedThreadPool();
    targets.forEach(
        ip ->
            executor.submit(
                () -> {
                  JmxManager eachJmx = getJmx(ip, config.getJmxPort());
                  eachJmx.clearSnapshot(null, keyspaces);
                  eachJmx.takeSnapshot(backupId, keyspaces);
                }));
    awaitTermination(executor, "takeNodesSnapshots");
  }

  private void uploadNodesBackups(String backupId, List<String> targets, BackupRequest request) {
    // Parallel upload for now. It will be adjusted based on workload
    ExecutorService executor = Executors.newCachedThreadPool();
    targets.forEach(
        ip ->
            executor.submit(
                () -> {
                  if (request.getBackupType() != BackupType.NODE_INCREMENTAL.get()) {
                    removeIncremental(ip);
                  }
                  uploadNodeBackups(backupId, ip, request);
                }));
    awaitTermination(executor, "copyNodesBackups");
  }

  @VisibleForTesting
  void removeIncremental(String ip) {
    // TODO: remove incremental backups (coming in a later PR)
  }

  @VisibleForTesting
  void uploadNodeBackups(String backupId, String ip, BackupRequest request) {
    List<String> arguments =
        Arrays.asList(
            CLUSTER_ID_OPTION + jmx.getClusterName(),
            BACKUP_ID_OPTION + backupId,
            TARGET_IP_OPTION + ip,
            DATA_DIR_OPTION + jmx.getAllDataFileLocations().get(0),
            STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
            KEYSPACES_OPTION + Joiner.on(',').join(getTargetKeyspaces(request)),
            BACKUP_TYPE_OPTION + request.getBackupType());

    RemoteCommand command =
        RemoteCommand.newBuilder()
            .ip(ip)
            .username(config.getUserName())
            .privateKeyFile(Paths.get(config.getPrivateKeyPath()))
            .name(BACKUP_COMMAND)
            .command(config.getSlaveCommandPath() + "/" + BACKUP_COMMAND)
            .arguments(arguments)
            .build();

    executor.execute(command);
  }

  private List<String> getTargetKeyspaces(BackupRequest request) {
    List<String> keyspaces;
    if (request.getKeyspacesList().isEmpty()) {
      keyspaces = jmx.getKeyspaces();
    } else {
      keyspaces = request.getKeyspacesList();
    }
    return keyspaces;
  }
}
