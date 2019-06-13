package com.scalar.backup.cassandra.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.RestoreRequest;
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

  @Test
  public void restoreBackup_ClusterTypeGiven_DownloadToEveryNode() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    RestoreRequest request =
        RestoreRequest.newBuilder().setRestoreType(RestoreType.CLUSTER.get()).build();
    doNothing().when(master).downloadNodeBackups(anyString(), any(RestoreRequest.class));

    // Act
    master.restoreBackup(request);

    // Assert
    nodes.forEach(ip -> verify(master).downloadNodeBackups(ip, request));
  }

  @Test
  public void restoreBackup_ClusterTypeAndNodesGiven_DownloadToTheNodes() {
    // Arrange
    when(config.getJmxPort()).thenReturn(JMX_PORT);
    when(jmx.getKeyspaces()).thenReturn(keyspaces);
    prepareNodes(jmx, nodes);
    List<String> someNodes = Arrays.asList(nodes.get(0));
    RestoreRequest request =
        RestoreRequest.newBuilder()
            .setRestoreType(RestoreType.CLUSTER.get())
            .addAllTargetIps(someNodes)
            .build();
    doNothing().when(master).downloadNodeBackups(anyString(), any(RestoreRequest.class));

    // Act
    master.restoreBackup(request);

    // Assert
    someNodes.forEach(ip -> verify(master).downloadNodeBackups(ip, request));
  }
}
