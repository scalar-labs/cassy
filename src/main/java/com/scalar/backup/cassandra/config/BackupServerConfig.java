package com.scalar.backup.cassandra.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupServerConfig {
  private final Properties props;
  protected static final String PREFIX = "scalar.backup.cassandra.server.";
  public static final String PORT = PREFIX + "port";
  public static final String JMX_PORT = PREFIX + "jmx_port";
  public static final String USER_NAME = PREFIX + "user_name";
  public static final String PRIVATE_KEY_PATH = PREFIX + "private_key_path";
  public static final String CASSANDRA_HOST = PREFIX + "cassandra_host";
  public static final String SCALAR_HOSTS = PREFIX + "scalar_hosts";
  public static final String SLAVE_COMMAND_PATH = PREFIX + "slave_command_path";
  public static final String STORAGE_BASE_URI = PREFIX + "storage_base_uri";
  public static final String DB_URL = PREFIX + "db_url";
  public static final String SRV_SERVICE_URL = PREFIX + "srv_service_url";
  private int port = 20051;
  private int jmxPort = 7199;
  private String userName;
  private String privateKeyPath;
  private String cassandraHost;
  private String scalarHosts;
  private String slaveCommandPath;
  private String storageBaseUri;
  private String dbUrl;
  private String srvServiceUrl;

  public BackupServerConfig(File propertiesFile) throws IOException {
    this(new FileInputStream(propertiesFile));
  }

  public BackupServerConfig(InputStream stream) throws IOException {
    props = new Properties();
    props.load(stream);
    load();
  }

  public BackupServerConfig(Properties properties) {
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

  public String getUserName() {
    return userName;
  }

  public String getPrivateKeyPath() {
    return privateKeyPath;
  }

  public String getCassandraHost() {
    return cassandraHost;
  }

  public String getScalarHosts() {
    return scalarHosts;
  }

  public String getSlaveCommandPath() {
    return slaveCommandPath;
  }

  public String getStorageBaseUri() {
    return storageBaseUri;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public String getSrvServiceUrl() {
    return srvServiceUrl;
  }

  private void load() {
    if (props.getProperty(PORT) != null) {
      port = Integer.parseInt(props.getProperty(PORT));
    }
    if (props.getProperty(JMX_PORT) != null) {
      jmxPort = Integer.parseInt(props.getProperty(JMX_PORT));
    }
    checkArgument(props.getProperty(USER_NAME) != null);
    userName = props.getProperty(USER_NAME);
    checkArgument(props.getProperty(PRIVATE_KEY_PATH) != null);
    privateKeyPath = props.getProperty(PRIVATE_KEY_PATH);
    checkArgument(props.getProperty(CASSANDRA_HOST) != null);
    cassandraHost = props.getProperty(CASSANDRA_HOST);
    checkArgument(props.getProperty(SCALAR_HOSTS) != null);
    scalarHosts = props.getProperty(SCALAR_HOSTS);
    checkArgument(props.getProperty(SLAVE_COMMAND_PATH) != null);
    slaveCommandPath = props.getProperty(SLAVE_COMMAND_PATH);
    checkArgument(props.getProperty(STORAGE_BASE_URI) != null);
    storageBaseUri = props.getProperty(STORAGE_BASE_URI);
    checkArgument(props.getProperty(DB_URL) != null);
    dbUrl = props.getProperty(DB_URL);
    checkArgument(props.getProperty(SRV_SERVICE_URL) != null);
    srvServiceUrl = props.getProperty(SRV_SERVICE_URL);
  }
}
