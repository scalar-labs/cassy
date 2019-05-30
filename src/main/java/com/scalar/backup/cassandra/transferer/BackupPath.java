package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.BaseConfig;
import java.nio.file.Paths;

public class BackupPath {
  private static final String CLUSTER_BACKUP_KEY = "cluster-backup";
  private static final String NODE_BACKUP_KEY = "node-backup";

  public static String create(BaseConfig config, String path) {
    String type = NODE_BACKUP_KEY;
    if (config.getBackupType().equals(BackupType.CLUSTER_SNAPSHOT)) {
      type = CLUSTER_BACKUP_KEY;
    }
    return Paths.get(type, config.getClusterId(), config.getBackupId(), config.getTargetIp(), path)
        .toString();
  }
}
