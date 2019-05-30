package com.scalar.backup.cassandra.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupConfig extends BaseConfig {
  public static final String BACKUP_TYPE = BaseConfig.PREFIX + "backup_type";
  private BackupType backupType;

  public BackupConfig(File propertiesFile) throws IOException {
    super(propertiesFile);
    load();
  }

  public BackupConfig(InputStream stream) throws IOException {
    super(stream);
    load();
  }

  public BackupConfig(Properties properties) {
    super(properties);
    load();
  }

  public BackupType getBackupType() {
    return backupType;
  }

  private void load() {
    Properties props = getProperties();
    checkArgument(props.getProperty(BACKUP_TYPE) != null);
    backupType = BackupType.getByType(Integer.parseInt(props.getProperty(BACKUP_TYPE)));
  }
}
