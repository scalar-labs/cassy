package com.scalar.backup.cassandra.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RestoreConfig extends BaseConfig {

  public RestoreConfig(File propertiesFile) throws IOException {
    super(propertiesFile);
  }

  public RestoreConfig(InputStream stream) throws IOException {
    super(stream);
  }

  public RestoreConfig(Properties properties) {
    super(properties);
  }
}
