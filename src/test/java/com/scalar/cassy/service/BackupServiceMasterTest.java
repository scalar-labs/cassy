package com.scalar.cassy.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.db.ClusterInfoRecord;
import com.scalar.cassy.jmx.JmxManager;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class BackupServiceMasterTest {
  private static final String CLUSTER_ID = "cluster_id";
  private static final String SNAPSHOT_ID = "snapshot_id";
  private static final long CREATED_AT = 1L;
  private static final int JMX_PORT = 7199;
  private static final String NODE1 = "192.168.1.1";
  private static final String NODE2 = "192.168.1.2";
  private static final String NODE3 = "192.168.1.3";
  private static final List<String> nodes = Arrays.asList(NODE1, NODE2, NODE3);
  private static final String KEYSPACE1 = "keyspace1";
  private static final String KEYSPACE2 = "keyspace2";
  private static final String KEYSPACE3 = "keyspace3";
  private static final List<String> keyspaces = Arrays.asList(KEYSPACE1, KEYSPACE2, KEYSPACE3);
  @Mock private CassyServerConfig config;
  @Mock private ClusterInfoRecord clusterInfo;
  @Mock private RemoteCommandExecutor executor;
  @Mock private ApplicationPauser pauser;
  @Spy @InjectMocks private BackupServiceMaster master;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  private List<BackupKey> prepareBackupKeys(
      String clusterId, List<String> nodes, String snaphotId, long createdAt) {
    List<BackupKey> backupKeys = new ArrayList<>();
    nodes.forEach(
        n ->
            backupKeys.add(
                BackupKey.newBuilder()
                    .clusterId(clusterId)
                    .targetIp(n)
                    .snapshotId(snaphotId)
                    .createdAt(createdAt)
                    .build()));
    return backupKeys;
  }

  @Test
  public void takeBackup_ClusterSnapshotTypeGiven_takeSnapshotsAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(clusterInfo.getKeyspaces()).thenReturn(keyspaces);
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, nodes, SNAPSHOT_ID, CREATED_AT);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .uploadNodeBackups(any(BackupKey.class), any(BackupType.class));
    doNothing().when(pauser).pause();
    doNothing().when(pauser).unpause();

    // Act
    master.takeBackup(keys, BackupType.CLUSTER_SNAPSHOT);

    // Assert
    nodes.forEach(ip -> verify(master).getJmx(ip, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(SNAPSHOT_ID, keyspaces.toArray(new String[0]));
    keys.forEach(key -> verify(master).uploadNodeBackups(key, BackupType.CLUSTER_SNAPSHOT));
  }

  @Test
  public void takeBackup_NodeSnapshotTypeGiven_takeSnapshotsAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(clusterInfo.getKeyspaces()).thenReturn(keyspaces);
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, nodes, SNAPSHOT_ID, CREATED_AT);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .uploadNodeBackups(any(BackupKey.class), any(BackupType.class));

    // Act
    master.takeBackup(keys, BackupType.NODE_SNAPSHOT);

    // Assert
    nodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(SNAPSHOT_ID, keyspaces.toArray(new String[0]));
    keys.forEach(key -> verify(master).uploadNodeBackups(key, BackupType.NODE_SNAPSHOT));
  }

  @Test
  public void takeBackup_NodeSnapshotTypeAndNodesGiven_takeSnapshotsAndUploadOnTheNodes() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(clusterInfo.getKeyspaces()).thenReturn(keyspaces);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, someNodes, SNAPSHOT_ID, CREATED_AT);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .uploadNodeBackups(any(BackupKey.class), any(BackupType.class));

    // Act
    master.takeBackup(keys, BackupType.NODE_SNAPSHOT);

    // Assert
    someNodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(someNodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(someNodes.size()))
        .takeSnapshot(SNAPSHOT_ID, keyspaces.toArray(new String[0]));
    keys.forEach(key -> verify(master).uploadNodeBackups(key, BackupType.NODE_SNAPSHOT));
  }

  @Test
  public void takeBackup_NodeIncrementalTypeGiven_UploadOnEveryNode() {
    // Arrange
    when(clusterInfo.getKeyspaces()).thenReturn(keyspaces);
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, nodes, SNAPSHOT_ID, CREATED_AT);
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .uploadNodeBackups(any(BackupKey.class), any(BackupType.class));

    // Act
    master.takeBackup(keys, BackupType.NODE_INCREMENTAL);

    // Assert
    verify(master, never()).getJmx(anyString(), anyInt());
    keys.forEach(key -> verify(master).uploadNodeBackups(key, BackupType.NODE_INCREMENTAL));
  }

  @Test
  public void takeBackup_NodeIncrementalTypeAndNodesGiven_UploadOnEveryTheNodes() {
    // Arrange
    when(clusterInfo.getKeyspaces()).thenReturn(keyspaces);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, someNodes, SNAPSHOT_ID, CREATED_AT);
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .uploadNodeBackups(any(BackupKey.class), any(BackupType.class));

    // Act
    master.takeBackup(keys, BackupType.NODE_INCREMENTAL);

    // Assert
    verify(master, never()).getJmx(anyString(), anyInt());
    keys.forEach(key -> verify(master).uploadNodeBackups(key, BackupType.NODE_INCREMENTAL));
  }
}
