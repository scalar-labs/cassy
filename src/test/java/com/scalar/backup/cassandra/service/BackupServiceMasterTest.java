package com.scalar.backup.cassandra.service;

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

import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class BackupServiceMasterTest {
  private static final String ANY_BACKUP_ID = "backup_id";
  private static final int JMX_PORT = 7199;
  private static final String NODE1 = "192.168.1.1";
  private static final String NODE2 = "192.168.1.2";
  private static final String NODE3 = "192.168.1.3";
  private static final List<String> nodes = Arrays.asList(NODE1, NODE2, NODE3);
  private static final String KEYSPACE1 = "keyspace1";
  private static final String KEYSPACE2 = "keyspace2";
  private static final String KEYSPACE3 = "keyspace3";
  private static final List<String> keyspaces = Arrays.asList(KEYSPACE1, KEYSPACE2, KEYSPACE3);
  @Mock private BackupServerConfig config;
  @Mock private JmxManager jmx;
  @Mock private RemoteCommandExecutor executor;
  @Spy @InjectMocks private BackupServiceMaster master;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  private void prepareNodes(JmxManager mockJmx, List<String> liveNodes) {
    when(mockJmx.getLiveNodes()).thenReturn(liveNodes);
    when(mockJmx.getJoiningNodes()).thenReturn(Lists.emptyList());
    when(mockJmx.getMovingNodes()).thenReturn(Lists.emptyList());
    when(mockJmx.getLeavingNodes()).thenReturn(Lists.emptyList());
    when(mockJmx.getUnreachableNodes()).thenReturn(Lists.emptyList());
  }

  @Test
  public void takeBackup_ClusterSnapshotTypeGiven_takeSnapshotsAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.CLUSTER_SNAPSHOT.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));
    doNothing().when(master).removeIncremental(anyString());

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    nodes.forEach(ip -> verify(master).getJmx(ip, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(ANY_BACKUP_ID, keyspaces.toArray(new String[0]));
    nodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    nodes.forEach(ip -> verify(master).removeIncremental(ip));
  }

  @Test
  public void
      takeBackup_ClusterSnapshotTypeAndKeyspacesGiven_takeSnapshotsForTheKeyspacesAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    List<String> someKeyspaces = Arrays.asList(keyspaces.get(0));
    prepareNodes(jmx, nodes);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    BackupRequest request =
        BackupRequest.newBuilder()
            .setBackupType(BackupType.CLUSTER_SNAPSHOT.get())
            .addAllKeyspaces(someKeyspaces)
            .build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));
    doNothing().when(master).removeIncremental(anyString());

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    nodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, someKeyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(ANY_BACKUP_ID, someKeyspaces.toArray(new String[0]));
  }

  @Test
  public void takeBackup_NodeSnapshotTypeGiven_takeSnapshotsAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.NODE_SNAPSHOT.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));
    doNothing().when(master).removeIncremental(anyString());

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    nodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(ANY_BACKUP_ID, keyspaces.toArray(new String[0]));
    nodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    nodes.forEach(ip -> verify(master).removeIncremental(ip));
  }

  @Test
  public void
      takeBackup_NodeSnapshotTypeAndKeyspacesGiven_takeSnapshotsForTheKeyspacesAndUploadOnEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    List<String> someKeyspaces = Arrays.asList(keyspaces.get(0));
    when(jmx.getKeyspaces()).thenReturn(someKeyspaces);
    prepareNodes(jmx, nodes);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.NODE_SNAPSHOT.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));
    doNothing().when(master).removeIncremental(anyString());

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    nodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(nodes.size())).clearSnapshot(null, someKeyspaces.toArray(new String[0]));
    verify(eachJmx, times(nodes.size()))
        .takeSnapshot(ANY_BACKUP_ID, someKeyspaces.toArray(new String[0]));
    nodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    nodes.forEach(ip -> verify(master).removeIncremental(ip));
  }

  @Test
  public void takeBackup_NodeSnapshotTypeAndNodesGiven_takeSnapshotsAndUploadOnTheNodes() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    prepareNodes(jmx, someNodes);
    JmxManager eachJmx = mock(JmxManager.class);
    doReturn(eachJmx).when(master).getJmx(anyString(), anyInt());
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.NODE_SNAPSHOT.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));
    doNothing().when(master).removeIncremental(anyString());

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    someNodes.forEach(n -> verify(master).getJmx(n, JMX_PORT));
    verify(eachJmx, times(someNodes.size())).clearSnapshot(null, keyspaces.toArray(new String[0]));
    verify(eachJmx, times(someNodes.size()))
        .takeSnapshot(ANY_BACKUP_ID, keyspaces.toArray(new String[0]));
    someNodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    someNodes.forEach(ip -> verify(master).removeIncremental(ip));
  }

  @Test
  public void takeBackup_NodeIncrementalTypeGiven_UploadOnEveryNode() {
    // Arrange
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.NODE_INCREMENTAL.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    verify(master, never()).getJmx(anyString(), anyInt());
    nodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    verify(master, never()).removeIncremental(anyString());
  }

  @Test
  public void takeBackup_NodeIncrementalTypeAndNodesGiven_UploadOnEveryTheNodes() {
    // Arrange
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    prepareNodes(jmx, someNodes);
    BackupRequest request =
        BackupRequest.newBuilder().setBackupType(BackupType.NODE_INCREMENTAL.get()).build();
    doNothing().when(master).uploadNodeBackups(anyString(), anyString(), any(BackupRequest.class));

    // Act
    master.takeBackup(ANY_BACKUP_ID, request);

    // Assert
    verify(master, never()).getJmx(anyString(), anyInt());
    someNodes.forEach(ip -> verify(master).uploadNodeBackups(ANY_BACKUP_ID, ip, request));
    verify(master, never()).removeIncremental(anyString());
  }
}
