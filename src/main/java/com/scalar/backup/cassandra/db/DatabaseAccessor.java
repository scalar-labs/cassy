package com.scalar.backup.cassandra.db;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseAccessor {
  private final BackupHistory backupHistory;
  private final RestoreHistory restoreHistory;
  private final ClusterInfo clusterInfo;

  public DatabaseAccessor(
      BackupHistory backupHistory, RestoreHistory restoreHistory, ClusterInfo clusterInfo) {
    this.backupHistory = backupHistory;
    this.restoreHistory = restoreHistory;
    this.clusterInfo = clusterInfo;
  }

  public BackupHistory getBackupHistory() {
    return backupHistory;
  }

  public RestoreHistory getRestoreHistory() {
    return restoreHistory;
  }

  public ClusterInfo getClusterInfo() {
    return clusterInfo;
  }
}
