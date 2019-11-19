package com.scalar.cassy.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.db.ClusterInfoRecord;
import com.scalar.cassy.exception.BackupException;
import com.scalar.cassy.exception.TimeoutException;
import com.scalar.cassy.jmx.JmxManager;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServiceMaster {
  protected final String CLUSTER_ID_OPTION = "--cluster-id=";
  protected final String SNAPSHOT_ID_OPTION = "--snapshot-id=";
  protected final String TARGET_IP_OPTION = "--target-ip=";
  protected final String DATA_DIR_OPTION = "--data-dir=";
  protected final String STORE_BASE_URI_OPTION = "--store-base-uri=";
  protected final String STORE_TYPE_OPTION = "--store-type=";
  protected final String KEYSPACES_OPTION = "--keyspaces=";
  protected final CassyServerConfig config;
  protected final ClusterInfoRecord clusterInfo;
  protected final RemoteCommandExecutor executor;

  public AbstractServiceMaster(
      CassyServerConfig config, ClusterInfoRecord clusterInfo, RemoteCommandExecutor executor) {
    this.config = config;
    this.clusterInfo = clusterInfo;
    this.executor = executor;
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
