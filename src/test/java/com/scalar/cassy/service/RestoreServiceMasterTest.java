package com.scalar.cassy.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.cassy.config.BackupServerConfig;
import com.scalar.cassy.config.RestoreType;
import com.scalar.cassy.jmx.JmxManager;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class RestoreServiceMasterTest {
  private static final int JMX_PORT = 7199;
  private static final String CLUSTER_ID = "cluster_id";
  private static final String SNAPSHOT_ID = "snapshot_id";
  private static final long CREATED_AT = 1L;
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
  @Spy @InjectMocks private RestoreServiceMaster master;

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
  public void restoreBackup_ClusterTypeGiven_DownloadToEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, nodes, SNAPSHOT_ID, CREATED_AT);
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .downloadNodeBackups(any(BackupKey.class), any(RestoreType.class), anyBoolean());

    // Act
    master.restoreBackup(keys, RestoreType.CLUSTER);

    // Assert
    keys.forEach(key -> verify(master).downloadNodeBackups(key, RestoreType.CLUSTER, false));
  }

  @Test
  public void restoreBackup_ClusterTypeAndNodesGiven_DownloadToTheNodes() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, someNodes, SNAPSHOT_ID, CREATED_AT);
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .downloadNodeBackups(any(BackupKey.class), any(RestoreType.class), anyBoolean());

    // Act
    master.restoreBackup(keys, RestoreType.NODE);

    // Assert
    keys.forEach(key -> verify(master).downloadNodeBackups(key, RestoreType.NODE, false));
  }

  @Test
  public void restoreBackup_SnapshotOnlyGiven_DownloadWithSnapshotOnlyOption() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    List<BackupKey> keys = prepareBackupKeys(CLUSTER_ID, nodes, SNAPSHOT_ID, CREATED_AT);
    doReturn(mock(RemoteCommandContext.class))
        .when(master)
        .downloadNodeBackups(any(BackupKey.class), any(RestoreType.class), anyBoolean());

    // Act
    master.restoreBackup(keys, RestoreType.NODE, true);

    // Assert
    keys.forEach(key -> verify(master).downloadNodeBackups(key, RestoreType.NODE, true));
  }
}
