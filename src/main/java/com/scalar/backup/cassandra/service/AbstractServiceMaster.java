package com.scalar.backup.cassandra.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.exception.BackupException;
import com.scalar.backup.cassandra.exception.TimeoutException;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServiceMaster {
  protected final String CLUSTER_ID_OPTION = "--cluster-id=";
  protected final String BACKUP_ID_OPTION = "--backup-id=";
  protected final String TARGET_IP_OPTION = "--target-ip=";
  protected final String DATA_DIR_OPTION = "--data-dir=";
  protected final String STORE_BASE_URI_OPTION = "--store-base-uri=";
  protected final String KEYSPACES_OPTION = "--keyspaces=";
  protected final BackupServerConfig config;
  protected final JmxManager jmx;
  protected final RemoteCommandExecutor executor;

  public AbstractServiceMaster(
      BackupServerConfig config, JmxManager jmx, RemoteCommandExecutor executor) {
    this.config = config;
    this.jmx = jmx;
    this.executor = executor;
  }

  protected boolean areAllNodesAlive() {
    if (jmx.getJoiningNodes().isEmpty()
        && jmx.getMovingNodes().isEmpty()
        && jmx.getLeavingNodes().isEmpty()
        && jmx.getUnreachableNodes().isEmpty()
        && jmx.getLiveNodes().size() > 0) {
      return true;
    }
    return false;
  }

  protected void awaitTermination(ExecutorService executor, String tag) {
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

  @VisibleForTesting
  JmxManager getJmx(String ip, int port) {
    return new JmxManager(ip, port);
  }
}
