package com.scalar.backup.cassandra.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RestoreConfig extends BaseConfig {
  public static final String RESTORE_TYPE = BaseConfig.PREFIX + "restore_type";
  public static final String SNAPSHOT_ONLY = BaseConfig.PREFIX + "snapshot_only";
  private RestoreType restoreType;
  private boolean snapshotOnly = false;

  public RestoreConfig(File propertiesFile) throws IOException {
    super(propertiesFile);
    load();
  }

  public RestoreConfig(InputStream stream) throws IOException {
    super(stream);
    load();
  }

  public RestoreConfig(Properties properties) {
    super(properties);
    load();
  }

  public RestoreType getRestoreType() {
    return restoreType;
  }

  public boolean isSnapshotOnly() {
    return snapshotOnly;
  }

  private void load() {
    Properties props = getProperties();
    checkArgument(props.getProperty(RESTORE_TYPE) != null);
    restoreType = RestoreType.getByType(Integer.parseInt(props.getProperty(RESTORE_TYPE)));
    if (props.getProperty(SNAPSHOT_ONLY) != null) {
      snapshotOnly = Boolean.parseBoolean(props.getProperty(SNAPSHOT_ONLY));
    }
  }
}
