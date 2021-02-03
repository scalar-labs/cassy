package com.scalar.cassy.db;

import com.google.inject.Inject;
import java.sql.Connection;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseAccessor {
  private final BackupHistory backupHistory;
  private final RestoreHistory restoreHistory;
  private final ClusterInfo clusterInfo;

  @Inject
  public DatabaseAccessor(Connection connection) {
    this.backupHistory = new BackupHistory(connection);
    this.restoreHistory = new RestoreHistory(connection);
    this.clusterInfo = new ClusterInfo(connection);
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
