package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.protobuf.ByteString;
import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.CommandFuture;
import com.palantir.giraffe.command.CommandResult;
import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.exception.BackupException;
import com.scalar.backup.cassandra.exception.TimeoutException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupServiceMaster {
  private static final Logger logger = LoggerFactory.getLogger(BackupServiceMaster.class.getName());
  private final String CLUSTER_ID_OPTION = "--cluster-id=";
  private final String BACKUP_ID_OPTION = "--backup-id=";
  private final String TARGET_IP_OPTION = "--target-ip=";
  private final String DATA_DIR_OPTION = "--data-dir=";
  private final String STORE_BASE_URI_OPTION = "--store-base-uri=";
  private final String KEYSPACES_OPTION = "--keyspaces=";
  private final String BACKUP_TYPE_OPTION = "--backup-type=";
  private final String BACKUP_COMMAND = "cassandra-backup";
  private final BackupServerConfig config;
  private final JmxManager jmx;
  private final SshCredential credential;

  public BackupServiceMaster(BackupServerConfig config, JmxManager jmx, SshCredential credential) {
    this.config = config;
    this.jmx = jmx;
    this.credential = credential;
  }

  public void take(String backupId, BackupRequest request) {
    if (!areAllNodesAlive()) {
      throw new BackupException(
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

  private boolean areAllNodesAlive() {
    if (jmx.getJoiningNodes().isEmpty()
        && jmx.getMovingNodes().isEmpty()
        && jmx.getLeavingNodes().isEmpty()
        && jmx.getUnreachableNodes().isEmpty()
        && jmx.getLiveNodes().size() > 0) {
      return true;
    }
    return false;
  }

  private void takeClusterSnapshots(String backupId, BackupRequest request) {
    // 1. TODO: stop DLs (coming in a later PR)

    // 2. take snapshots of all the nodes
    takeNodesSnapshots(backupId, jmx.getLiveNodes(), request);

    // 3. TODO: start DLs (coming in a later PR)

    // 4. copy snapshots in parallel
    copyNodesBackups(backupId, jmx.getLiveNodes(), request);
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
    copyNodesBackups(backupId, targets, request);
  }

  private void takeNodesSnapshots(String backupId, List<String> targets, BackupRequest request) {
    String[] keyspaces = getTargetKeyspaces(request).toArray(new String[0]);

    ExecutorService executor = Executors.newCachedThreadPool();
    targets.forEach(
        ip ->
            executor.submit(
                () -> {
                  JmxManager eachJmx = getJmx(ip, config.getJmxPort());
                  eachJmx.clearSnapshot(backupId, keyspaces);
                  eachJmx.takeSnapshot(backupId, keyspaces);
                }));
    awaitTermination(executor, "takeNodesSnapshots");
  }

  private void copyNodesBackups(String backupId, List<String> targets, BackupRequest request) {
    // Parallel upload for now. It will be adjusted based on workload
    ExecutorService executor = Executors.newCachedThreadPool();
    targets.forEach(ip -> executor.submit(() -> copyNodeBackups(backupId, ip, request)));
    awaitTermination(executor, "copyNodesBackups");
  }

  private void awaitTermination(ExecutorService executor, String tag) {
    executor.shutdown();
    try {
      boolean terminated = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
      if (!terminated) {
        throw new TimeoutException("timeout occurred in " + tag);
      }
    } catch (InterruptedException e) {
      throw new BackupException(e);
    }
  }

  private void copyNodeBackups(String backupId, String ip, BackupRequest request) {
    List<String> keyspaces = getTargetKeyspaces(request);
    SshHostAccessor ssh = SshHostAccessor.forCredential(Host.fromHostname(ip), credential);

    try (HostControlSystem hcs = ssh.open()) {
      if (request.getBackupType() != BackupType.NODE_INCREMENTAL.get()) {
        // TODO: remove incremental backups (coming in a later PR)
      }

      Command.Builder builder =
          hcs.getExecutionSystem()
              .getCommandBuilder(config.getSlaveCommandPath() + "/" + BACKUP_COMMAND);
      builder.addArguments(
          CLUSTER_ID_OPTION + jmx.getClusterName(),
          BACKUP_ID_OPTION + backupId,
          TARGET_IP_OPTION + ip,
          DATA_DIR_OPTION + jmx.getAllDataFileLocations().get(0),
          STORE_BASE_URI_OPTION + config.getStorageBaseUri(),
          KEYSPACES_OPTION + Joiner.on(',').join(keyspaces),
          BACKUP_TYPE_OPTION + request.getBackupType());

      Command command = builder.build();
      logger.info("executing " + command.toString());
      CommandFuture future = Commands.executeAsync(command);

      // TODO: update status (coming in a later PR)

      CommandResult result = Commands.waitFor(future);
      logger.info(Integer.toString(result.getExitStatus()));
      if (result.getExitStatus() != 0) {
        throw new BackupException(BACKUP_COMMAND + " failed for some reason");
      }
      logger.debug(result.getStdOut());
    } catch (IOException e) {
      throw new BackupException(e);
    }
  }

  private List<String> getTargetKeyspaces(BackupRequest request) {
    List<String> keyspaces;
    if (request.getKeyspacesList().isEmpty()) {
      keyspaces = jmx.getKeyspaces();
    } else {
      keyspaces =
          request
              .getKeyspacesList()
              .asByteStringList()
              .stream()
              .map(ByteString::toString)
              .collect(Collectors.toList());
    }
    return keyspaces;
  }

  @VisibleForTesting
  JmxManager getJmx(String ip, int port) {
    return new JmxManager(ip, port);
  }
}
