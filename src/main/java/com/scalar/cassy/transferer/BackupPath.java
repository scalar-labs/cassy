package com.scalar.cassy.transferer;

import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.BaseConfig;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.config.RestoreType;
import java.nio.file.Paths;

public class BackupPath {
  private static final String CLUSTER_BACKUP_KEY = "cluster-backup";
  private static final String NODE_BACKUP_KEY = "node-backup";

  public static String create(BaseConfig config, String path) {
    return create(getType(config), config, path);
  }

  public static String getType(BaseConfig config) {
    if (config instanceof RestoreConfig) {
      RestoreConfig restoreConfig = (RestoreConfig) config;
      String type = NODE_BACKUP_KEY;
      if (restoreConfig.getRestoreType().equals(RestoreType.CLUSTER)) {
        type = CLUSTER_BACKUP_KEY;
      }
      return type;
    }
    if (config instanceof BackupConfig) {
      BackupConfig backupConfig = (BackupConfig) config;
      String type = NODE_BACKUP_KEY;
      if (backupConfig.getBackupType().equals(BackupType.CLUSTER_SNAPSHOT)) {
        type = CLUSTER_BACKUP_KEY;
      }
      return type;
    }
    throw new UnsupportedOperationException(
        "The " + config.getClass().getSimpleName() + " config type is not supported");
  }

  private static String create(String type, BaseConfig config, String path) {
    return Paths.get(
            type, config.getClusterId(), config.getSnapshotId(), config.getTargetIp(), path)
        .toString();
  }
}
