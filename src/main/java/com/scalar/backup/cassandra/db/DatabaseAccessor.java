package com.scalar.backup.cassandra.db;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseAccessor {
  private final BackupHistory backupHistory;
  private final ClusterInfo clusterInfo;

  public DatabaseAccessor(BackupHistory backupHistory, ClusterInfo clusterInfo) {
    this.backupHistory = backupHistory;
    this.clusterInfo = clusterInfo;
  }

  public BackupHistory getBackupHistory() {
    return backupHistory;
  }

  public ClusterInfo getClusterInfo() {
    return clusterInfo;
  }
}
