package com.scalar.backup.cassandra.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.exception.JmxException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import org.apache.cassandra.service.StorageServiceMBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class JmxManagerTest {
  private static final List<String> ANY_NODE_LIST = Arrays.asList("node");
  private static final String ANY_TAG = "tag";
  private static final String ANY_KEYSPACE = "keyspace";
  private static final List<String> ANY_KEYSPACE_LIST = Arrays.asList("keyspace");
  private static final int ANY_NUMBER = 10;
  private static final String[] ANY_DATADIR_ARRAY = {"data_dir"};
  private static final String ANY_CLUSTER_NAME = "cluster";
  @Mock private StorageServiceMBean storageService;
  @Mock private JMXConnector connector;
  @Mock private MBeanServerConnection connection;
  @Spy @InjectMocks private JmxManager manager;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(manager.loadStorageServiceIfNotLoaded()).thenReturn(storageService);
  }

  @Test
  public void getJoiningNodes_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getJoiningNodes()).thenReturn(ANY_NODE_LIST);

    // Assert
    List<String> actual = manager.getJoiningNodes();

    // Act
    verify(storageService).getJoiningNodes();
    assertThat(actual).isEqualTo(ANY_NODE_LIST);
  }

  @Test
  public void getLeavingNodes_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getLeavingNodes()).thenReturn(ANY_NODE_LIST);

    // Assert
    List<String> actual = manager.getLeavingNodes();

    // Act
    verify(storageService).getLeavingNodes();
    assertThat(actual).isEqualTo(ANY_NODE_LIST);
  }

  @Test
  public void getMovingNodes_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getMovingNodes()).thenReturn(ANY_NODE_LIST);

    // Assert
    List<String> actual = manager.getMovingNodes();

    // Act
    verify(storageService).getMovingNodes();
    assertThat(actual).isEqualTo(ANY_NODE_LIST);
  }

  @Test
  public void getUnreachableNodes_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getUnreachableNodes()).thenReturn(ANY_NODE_LIST);

    // Assert
    List<String> actual = manager.getUnreachableNodes();

    // Act
    verify(storageService).getUnreachableNodes();
    assertThat(actual).isEqualTo(ANY_NODE_LIST);
  }

  @Test
  public void getLiveNodes_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getLiveNodes()).thenReturn(ANY_NODE_LIST);

    // Assert
    List<String> actual = manager.getLiveNodes();

    // Act
    verify(storageService).getLiveNodes();
    assertThat(actual).isEqualTo(ANY_NODE_LIST);
  }

  @Test
  public void takeSnapshot_TagAndKeyspaceGiven_ShouldDelegateProperly() throws IOException {
    // Arrange
    Map<String, String> options = new HashMap<>();
    options.put("skipFlush", "false");

    // Assert
    manager.takeSnapshot(ANY_TAG, ANY_KEYSPACE);

    // Act
    verify(storageService).takeSnapshot(ANY_TAG, options, ANY_KEYSPACE);
  }

  @Test
  public void takeSnapshot_IOExceptionThrown_ShouldThrowJmxException() throws IOException {
    // Arrange
    Map<String, String> options = new HashMap<>();
    options.put("skipFlush", "false");
    IOException toThrow = mock(IOException.class);
    doThrow(toThrow).when(storageService).takeSnapshot(ANY_TAG, options, ANY_KEYSPACE);

    // Assert
    assertThatThrownBy(
            () -> {
              manager.takeSnapshot(ANY_TAG, ANY_KEYSPACE);
            })
        .isInstanceOf(JmxException.class)
        .hasCause(toThrow);

    // Act
    verify(storageService).takeSnapshot(ANY_TAG, options, ANY_KEYSPACE);
  }

  @Test
  public void clearSnapshot_TagAndKeyspaceGiven_ShouldDelegateProperly() throws IOException {
    // Arrange

    // Assert
    manager.clearSnapshot(ANY_TAG, ANY_KEYSPACE);

    // Act
    verify(storageService).clearSnapshot(ANY_TAG, ANY_KEYSPACE);
  }

  @Test
  public void clearSnapshot_IOExceptionThrown_ShouldThrowJmxExceptionProperly() throws IOException {
    // Arrange
    IOException toThrow = mock(IOException.class);
    doThrow(toThrow).when(storageService).clearSnapshot(ANY_TAG, ANY_KEYSPACE);

    // Assert
    assertThatThrownBy(
            () -> {
              manager.clearSnapshot(ANY_TAG, ANY_KEYSPACE);
            })
        .isInstanceOf(JmxException.class)
        .hasCause(toThrow);

    // Act
    verify(storageService).clearSnapshot(ANY_TAG, ANY_KEYSPACE);
  }

  @Test
  public void getKeyspaces_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getKeyspaces()).thenReturn(ANY_KEYSPACE_LIST);

    // Assert
    List<String> actual = manager.getKeyspaces();

    // Act
    verify(storageService).getKeyspaces();
    assertThat(actual).isEqualTo(ANY_KEYSPACE_LIST);
  }

  @Test
  public void getAllDataFileLocations_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getAllDataFileLocations()).thenReturn(ANY_DATADIR_ARRAY);

    // Assert
    List<String> actual = manager.getAllDataFileLocations();

    // Act
    verify(storageService).getAllDataFileLocations();
    assertThat(actual).isEqualTo(Arrays.asList(ANY_DATADIR_ARRAY));
  }

  @Test
  public void isIncrementalBackupsEnabled_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.isIncrementalBackupsEnabled()).thenReturn(true);

    // Assert
    boolean actual = manager.isIncrementalBackupsEnabled();

    // Act
    verify(storageService).isIncrementalBackupsEnabled();
    assertThat(actual).isTrue();
  }

  @Test
  public void getClusterName_NothingGiven_ShouldDelegateProperly() {
    // Arrange
    when(storageService.getClusterName()).thenReturn(ANY_CLUSTER_NAME);

    // Assert
    String actual = manager.getClusterName();

    // Act
    verify(storageService).getClusterName();
    assertThat(actual).isEqualTo(ANY_CLUSTER_NAME);
  }

  @Test
  public void getPendingCompactions_NothingGiven_ShouldDelegateProperly()
      throws AttributeNotFoundException, MBeanException, ReflectionException,
          InstanceNotFoundException, IOException {
    // Arrange
    when(connection.getAttribute(JmxManager.ObjectNames.COMPACTIONS_PENDING, "Value"))
        .thenReturn(ANY_NUMBER);

    // Assert
    int actual = manager.getPendingCompactions();

    // Act
    assertThat(actual).isEqualTo(ANY_NUMBER);
  }

  @Test
  public void getPendingCompactions_ExceptionThrown_ShouldThrowJmxException()
      throws AttributeNotFoundException, MBeanException, ReflectionException,
          InstanceNotFoundException, IOException {
    // Arrange
    MBeanException toThrow = mock(MBeanException.class);
    when(connection.getAttribute(JmxManager.ObjectNames.COMPACTIONS_PENDING, "Value"))
        .thenThrow(toThrow);

    // Assert
    assertThatThrownBy(() -> manager.getPendingCompactions())
        .isInstanceOf(JmxException.class)
        .hasCause(toThrow);

    // Act
  }

  @Test
  public void getClusterName_CalledTwice_ShouldLoadStorageServiceOnce() {
    // Arrange

    // Assert
    manager.getClusterName();
    manager.getClusterName();

    // Act
    verify(storageService, times(2)).getClusterName();
  }

  @Test
  public void close_TryWithResourceUsed_ShouldCloseProperly() throws IOException {
    // Arrange

    // Assert
    try (JmxManager manager = new JmxManager(connector, connection)) {}

    // Act
    verify(connector).close();
  }
}
