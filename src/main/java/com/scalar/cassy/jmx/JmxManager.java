package com.scalar.cassy.jmx;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.exception.JmxException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.cassandra.service.StorageServiceMBean;

@ThreadSafe
public class JmxManager implements AutoCloseable {
  private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
  private final JMXConnector connector;
  private final MBeanServerConnection connection;
  private StorageServiceMBean storageService;

  public JmxManager(String hostname, int port) {
    try {
      this.connector = JMXConnectorFactory.connect(getJmxUrl(hostname, port), null);
      this.connection = connector.getMBeanServerConnection();
    } catch (IOException e) {
      throw new JmxException(e);
    }
  }

  @VisibleForTesting
  JmxManager(JMXConnector connector, MBeanServerConnection connection) {
    this.connector = connector;
    this.connection = connection;
  }

  public List<String> getJoiningNodes() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getJoiningNodes();
  }

  public List<String> getLeavingNodes() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getLeavingNodes();
  }

  public List<String> getMovingNodes() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getMovingNodes();
  }

  public List<String> getUnreachableNodes() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getUnreachableNodes();
  }

  public List<String> getLiveNodes() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getLiveNodes();
  }

  public void takeSnapshot(String tag, String... keyspaces) {
    storageService = loadStorageServiceIfNotLoaded();
    Map<String, String> options = new HashMap<>();
    // always flush
    options.put("skipFlush", "false");
    try {
      storageService.takeSnapshot(tag, options, keyspaces);
    } catch (IOException e) {
      throw new JmxException(e);
    }
  }

  public void clearSnapshot(String tag, String... keyspaces) {
    storageService = loadStorageServiceIfNotLoaded();
    try {
      storageService.clearSnapshot(tag, keyspaces);
    } catch (IOException e) {
      throw new JmxException(e);
    }
  }

  public List<String> getKeyspaces() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getKeyspaces();
  }

  public List<String> getAllDataFileLocations() {
    storageService = loadStorageServiceIfNotLoaded();
    return Arrays.asList(storageService.getAllDataFileLocations());
  }

  public boolean isIncrementalBackupsEnabled() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.isIncrementalBackupsEnabled();
  }

  public String getClusterName() {
    storageService = loadStorageServiceIfNotLoaded();
    return storageService.getClusterName();
  }

  public int getPendingCompactions() {
    try {
      return (int) connection.getAttribute(ObjectNames.COMPACTIONS_PENDING, "Value");
    } catch (Exception e) {
      throw new JmxException(e);
    }
  }

  @Override
  public void close() {
    try {
      connector.close();
    } catch (IOException e) {
      throw new JmxException(e);
    }
  }

  @VisibleForTesting
  synchronized StorageServiceMBean loadStorageServiceIfNotLoaded() {
    if (storageService == null) {
      return JMX.newMBeanProxy(connection, ObjectNames.STORAGE_SERVICE, StorageServiceMBean.class);
    }
    return storageService;
  }

  private static JMXServiceURL getJmxUrl(String hostname, int port) {
    try {
      return new JMXServiceURL(String.format(JMX_URL, hostname, port));
    } catch (MalformedURLException e) {
      throw new JmxException(e);
    }
  }

  @VisibleForTesting
  static final class ObjectNames {
    static final ObjectName STORAGE_SERVICE;
    static final ObjectName COMPACTIONS_PENDING;

    static {
      try {
        STORAGE_SERVICE = new ObjectName("org.apache.cassandra.db:type=StorageService");
        COMPACTIONS_PENDING =
            new ObjectName("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks");
      } catch (MalformedObjectNameException e) {
        throw new IllegalStateException("Failure during preparations for JMX connection", e);
      }
    }
  }
}
