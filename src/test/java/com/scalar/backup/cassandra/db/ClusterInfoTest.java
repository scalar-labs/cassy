package com.scalar.backup.cassandra.db;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ClusterInfoTest {
  private final String CLUSTER_ID_ATTR = "cluster_id";
  private final String TARGET_IPS_ATTR = "target_ips";
  private final String KEYSPACES_ATTR = "keyspaces";
  private final String CLUSTER_ID = "cluster_id";
  private final List<String> TARGET_IPS = Arrays.asList("192.168.0.1", "192.168.0.2");
  private final List<String> KEYSPACES = Arrays.asList("k1", "k2");
  private final long TIMESTAMP = 1L;
  private final int N = 1;
  @Mock private Connection connection;
  @Mock private PreparedStatement insert;
  @Mock private PreparedStatement update;
  @Mock private PreparedStatement selectByCluster;
  @Mock private PreparedStatement selectRecent;
  private ClusterInfo clusterInfo;

  @Before
  public void setUp() throws SQLException {
    MockitoAnnotations.initMocks(this);

    when(connection.prepareStatement(ClusterInfo.INSERT)).thenReturn(insert);
    when(connection.prepareStatement(ClusterInfo.UPDATE)).thenReturn(update);
    when(connection.prepareStatement(ClusterInfo.SELECT_BY_CLUSTER)).thenReturn(selectByCluster);
    when(connection.prepareStatement(ClusterInfo.SELECT_RECENT)).thenReturn(selectRecent);
    doNothing().when(insert).setQueryTimeout(anyInt());
    doNothing().when(update).setQueryTimeout(anyInt());
    doNothing().when(selectByCluster).setQueryTimeout(anyInt());
    doNothing().when(selectRecent).setQueryTimeout(anyInt());

    // InjectMocks fails due to some dependency issue
    clusterInfo = new ClusterInfo(connection);
  }

  @Test
  public void insert_ProperArgumentsGiven_ShouldExecuteProperly() throws SQLException {
    // Arrange
    when(insert.executeUpdate()).thenReturn(1);
    doNothing().when(insert).clearParameters();

    // Act
    clusterInfo.insert(CLUSTER_ID, TARGET_IPS, KEYSPACES);

    // Assert
    verify(insert).setString(1, CLUSTER_ID);
    verify(insert).setString(2, String.join(",", TARGET_IPS));
    verify(insert).setString(3, String.join(",", KEYSPACES));
    verify(insert).executeUpdate();
    verify(insert).clearParameters();
  }

  @Test
  public void insert_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(insert.executeUpdate()).thenThrow(toThrow);
    doNothing().when(insert).clearParameters();

    // Act Assert
    assertThatThrownBy(() -> clusterInfo.insert(CLUSTER_ID, TARGET_IPS, KEYSPACES))
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);
  }

  @Test
  public void update_ProperArgumentsGiven_ShouldExecuteProperly() throws SQLException {
    // Arrange
    when(update.executeUpdate()).thenReturn(1);
    doNothing().when(update).clearParameters();

    // Act
    clusterInfo.update(CLUSTER_ID, TARGET_IPS, KEYSPACES);

    // Assert
    verify(update).setString(1, String.join(",", TARGET_IPS));
    verify(update).setString(2, String.join(",", KEYSPACES));
    verify(update).setString(4, CLUSTER_ID);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void update_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(update.executeUpdate()).thenThrow(toThrow);
    doNothing().when(update).clearParameters();

    // Act Assert
    assertThatThrownBy(() -> clusterInfo.update(CLUSTER_ID, TARGET_IPS, KEYSPACES))
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);
  }

  @Test
  public void selectByClusterId_ClusterIdGiven_ShouldReturnClusterInfoProperly()
      throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(selectByCluster.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(CLUSTER_ID_ATTR)).thenReturn(CLUSTER_ID);
    when(resultSet.getString(TARGET_IPS_ATTR)).thenReturn(String.join(",", TARGET_IPS));
    when(resultSet.getString(KEYSPACES_ATTR)).thenReturn(String.join(",", KEYSPACES));
    when(resultSet.getLong(anyString())).thenReturn(TIMESTAMP);
    when(resultSet.next()).thenReturn(true).thenReturn(false);

    // Act
    ClusterInfoRecord record = clusterInfo.selectByClusterId(CLUSTER_ID);

    // Assert
    verify(selectByCluster).clearParameters();
    verify(selectByCluster).setString(1, CLUSTER_ID);
    verify(selectByCluster).executeQuery();
    assertThat(record.getClusterId()).isEqualTo(CLUSTER_ID);
    assertThat(record.getTargetIps()).isEqualTo(TARGET_IPS);
    assertThat(record.getKeyspaces()).isEqualTo(KEYSPACES);
    assertThat(record.getCreatedAt()).isEqualTo(TIMESTAMP);
    assertThat(record.getUpdatedAt()).isEqualTo(TIMESTAMP);
  }

  @Test
  public void selectByClusterId_SQLExceptionThrown_ShouldThrowDatabaseException()
      throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(selectByCluster.executeQuery()).thenThrow(toThrow);

    // Act Assert
    assertThatThrownBy(() -> clusterInfo.selectByClusterId(CLUSTER_ID))
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);
  }

  @Test
  public void selectRecent_NumberGiven_ShouldReturnClusterInfoProperly() throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(selectRecent.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(CLUSTER_ID_ATTR)).thenReturn(CLUSTER_ID);
    when(resultSet.getString(TARGET_IPS_ATTR)).thenReturn(String.join(",", TARGET_IPS));
    when(resultSet.getString(KEYSPACES_ATTR)).thenReturn(String.join(",", KEYSPACES));
    when(resultSet.getLong(anyString())).thenReturn(TIMESTAMP);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<ClusterInfoRecord> records = clusterInfo.selectRecent(N);

    // Assert
    verify(selectRecent).clearParameters();
    verify(selectRecent).setInt(1, N);
    verify(selectRecent).executeQuery();
    records.forEach(
        r -> {
          assertThat(r.getClusterId()).isEqualTo(CLUSTER_ID);
          assertThat(r.getTargetIps()).isEqualTo(TARGET_IPS);
          assertThat(r.getKeyspaces()).isEqualTo(KEYSPACES);
          assertThat(r.getCreatedAt()).isEqualTo(TIMESTAMP);
          assertThat(r.getUpdatedAt()).isEqualTo(TIMESTAMP);
        });
    assertThat(records.size()).isEqualTo(2);
  }

  @Test
  public void selectRecent_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(selectRecent.executeQuery()).thenThrow(toThrow);

    // Act Assert
    assertThatThrownBy(() -> clusterInfo.selectRecent(N))
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);
  }
}
