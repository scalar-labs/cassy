package com.scalar.cassy.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BaseConfig {
  private final Properties props;
  protected static final String PREFIX = "scalar.backup.cassandra.";
  public static final String CLUSTER_ID = PREFIX + "cluster_id";
  public static final String SNAPSHOT_ID = PREFIX + "snapshot_id";
  public static final String TARGET_IP = PREFIX + "target_ip";
  public static final String DATA_DIR = PREFIX + "data_dir";
  public static final String STORE_BASE_URI = PREFIX + "store_base_uri";
  public static final String KEYSPACE = PREFIX + "keyspace";
  private String clusterId;
  private String snapshotId;
  private String targetIp;
  private String dataDir;
  private String storeBaseUri;
  private String keyspace;

  public BaseConfig(File propertiesFile) throws IOException {
    this(new FileInputStream(propertiesFile));
  }

  public BaseConfig(InputStream stream) throws IOException {
    props = new Properties();
    props.load(stream);
    load();
  }

  public BaseConfig(Properties properties) {
    props = new Properties(properties);
    load();
  }

  public Properties getProperties() {
    return props;
  }

  public String getClusterId() {
    return clusterId;
  }

  public String getSnapshotId() {
    return snapshotId;
  }

  public String getTargetIp() {
    return targetIp;
  }

  public String getDataDir() {
    return dataDir;
  }

  public String getStoreBaseUri() {
    return storeBaseUri;
  }

  public String getKeyspace() {
    return keyspace;
  }

  private void load() {
    checkArgument(props.getProperty(CLUSTER_ID) != null);
    clusterId = props.getProperty(CLUSTER_ID);
    checkArgument(props.getProperty(SNAPSHOT_ID) != null);
    snapshotId = props.getProperty(SNAPSHOT_ID);
    checkArgument(props.getProperty(TARGET_IP) != null);
    targetIp = props.getProperty(TARGET_IP);
    checkArgument(props.getProperty(DATA_DIR) != null);
    dataDir = props.getProperty(DATA_DIR);
    checkArgument(props.getProperty(STORE_BASE_URI) != null);
    storeBaseUri = props.getProperty(STORE_BASE_URI);
    checkArgument(props.getProperty(KEYSPACE) != null);
    keyspace = props.getProperty(KEYSPACE);
  }
}
