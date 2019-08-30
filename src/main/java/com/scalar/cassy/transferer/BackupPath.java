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

  public static String create(BackupConfig config, String path) {
    String type = NODE_BACKUP_KEY;
    if (config.getBackupType().equals(BackupType.CLUSTER_SNAPSHOT)) {
      type = CLUSTER_BACKUP_KEY;
    }
    return create(type, config, path);
  }

  public static String create(RestoreConfig config, String path) {
    String type = NODE_BACKUP_KEY;
    if (config.getRestoreType().equals(RestoreType.CLUSTER)) {
      type = CLUSTER_BACKUP_KEY;
    }
    return create(type, config, path);
  }

  private static String create(String type, BaseConfig config, String path) {
    return Paths.get(
            type, config.getClusterId(), config.getSnapshotId(), config.getTargetIp(), path)
        .toString();
  }
}
