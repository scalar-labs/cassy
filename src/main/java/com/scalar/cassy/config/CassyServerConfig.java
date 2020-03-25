package com.scalar.cassy.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class CassyServerConfig {
  protected static final String PREFIX = "scalar.cassy.server.";
  public static final String PORT = PREFIX + "port";
  public static final String JMX_PORT = PREFIX + "jmx_port";
  public static final String SSH_USER = PREFIX + "ssh_user";
  public static final String SSH_PRIVATE_KEY_PATH = PREFIX + "ssh_private_key_path";
  public static final String SLAVE_COMMAND_PATH = PREFIX + "slave_command_path";
  public static final String STORAGE_BASE_URI = PREFIX + "storage_base_uri";
  public static final String STORAGE_TYPE = PREFIX + "storage_type";
  public static final String METADATA_DB_URL = PREFIX + "metadata_db_url";
  public static final String SRV_SERVICE_URL = PREFIX + "srv_service_url";
  private final Properties props;
  private int port = 20051;
  private int jmxPort = 7199;
  private String sshUser;
  private String sshPrivateKeyPath;
  private String slaveCommandPath;
  private String storageBaseUri;
  private StorageType storageType;
  private String metadataDbUrl;
  private Optional<String> srvServiceUrl;

  public CassyServerConfig(File propertiesFile) throws IOException {
    this(new FileInputStream(propertiesFile));
  }

  public CassyServerConfig(InputStream stream) throws IOException {
    props = new Properties();
    props.load(stream);
    load();
  }

  public CassyServerConfig(Properties properties) {
    props = new Properties(properties);
    load();
  }

  public Properties getProperties() {
    return props;
  }

  public int getPort() {
    return port;
  }

  public int getJmxPort() {
    return jmxPort;
  }

  public String getSshUser() {
    return sshUser;
  }

  public String getSshPrivateKeyPath() {
    return sshPrivateKeyPath;
  }

  public String getSlaveCommandPath() {
    return slaveCommandPath;
  }

  public String getStorageBaseUri() {
    return storageBaseUri;
  }

  public StorageType getStorageType() {
    return storageType;
  }

  public String getMetadataDbUrl() {
    return metadataDbUrl;
  }

  public Optional<String> getSrvServiceUrl() {
    return srvServiceUrl;
  }

  private void load() {
    if (props.getProperty(PORT) != null) {
      port = Integer.parseInt(props.getProperty(PORT));
    }
    if (props.getProperty(JMX_PORT) != null) {
      jmxPort = Integer.parseInt(props.getProperty(JMX_PORT));
    }
    checkArgument(props.getProperty(SSH_USER) != null);
    sshUser = props.getProperty(SSH_USER);
    checkArgument(props.getProperty(SSH_PRIVATE_KEY_PATH) != null);
    sshPrivateKeyPath = props.getProperty(SSH_PRIVATE_KEY_PATH);
    checkArgument(props.getProperty(SLAVE_COMMAND_PATH) != null);
    slaveCommandPath = props.getProperty(SLAVE_COMMAND_PATH);
    checkArgument(props.getProperty(STORAGE_TYPE) != null);
    storageType = StorageType.valueOf(props.getProperty(STORAGE_TYPE).toUpperCase());
    checkArgument(props.getProperty(STORAGE_BASE_URI) != null);
    storageBaseUri = props.getProperty(STORAGE_BASE_URI);
    checkArgument(props.getProperty(METADATA_DB_URL) != null);
    metadataDbUrl = props.getProperty(METADATA_DB_URL);
    srvServiceUrl = Optional.ofNullable(props.getProperty(SRV_SERVICE_URL));
  }
}
