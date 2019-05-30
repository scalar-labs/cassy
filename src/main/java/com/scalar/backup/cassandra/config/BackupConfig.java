package com.scalar.backup.cassandra.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupConfig extends BaseConfig {

  public BackupConfig(File propertiesFile) throws IOException {
    super(propertiesFile);
  }

  public BackupConfig(InputStream stream) throws IOException {
    super(stream);
  }

  public BackupConfig(Properties properties) {
    super(properties);
  }
}
