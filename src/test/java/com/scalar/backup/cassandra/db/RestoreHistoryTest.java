package com.scalar.backup.cassandra.db;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.exception.DatabaseException;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest;
import com.scalar.backup.cassandra.service.BackupKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RestoreHistoryTest {
  private final String CLUSTER_ID = "cluster_id";
  private final String TARGET_IP = "127.0.0.1";
  private final RestoreType RESTORE_TYPE = RestoreType.NODE;
  private final String SNAPSHOT_ID = "snapshot_id";
  private final long CREATED_AT = 1L;
  private final OperationStatus RESTORE_STATUS = OperationStatus.COMPLETED;
  private final int N = 3;
  @Mock private Connection connection;
  @Mock private PreparedStatement insert;
  @Mock private PreparedStatement update;
  @Mock private PreparedStatement selectRecentByCluster;
  @Mock private PreparedStatement selectRecentByHost;
  @Mock private PreparedStatement selectBySnapshot;
  private RestoreHistory history;

  @Before
  public void setUp() throws SQLException {
    MockitoAnnotations.initMocks(this);

    when(connection.prepareStatement(RestoreHistory.INSERT)).thenReturn(insert);
    when(connection.prepareStatement(RestoreHistory.UPDATE)).thenReturn(update);
    when(connection.prepareStatement(RestoreHistory.SELECT_RECENT_BY_CLUSTER))
        .thenReturn(selectRecentByCluster);
    when(connection.prepareStatement(RestoreHistory.SELECT_RECENT_BY_HOST))
        .thenReturn(selectRecentByHost);
    when(connection.prepareStatement(RestoreHistory.SELECT_RECENT_BY_SNAPSHOT))
        .thenReturn(selectBySnapshot);
    doNothing().when(insert).setQueryTimeout(anyInt());
    doNothing().when(update).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByCluster).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByHost).setQueryTimeout(anyInt());
    doNothing().when(selectBySnapshot).setQueryTimeout(anyInt());

    // InjectMocks fails due to some dependency issue
    history = new RestoreHistory(connection);
  }

  @Test
  public void insert_ProperArgumentsGiven_ShouldExecuteProperly() throws SQLException {
    // Arrange
    when(insert.executeUpdate()).thenReturn(1);
    doNothing().when(insert).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .createdAt(CREATED_AT)
            .build();

    // Act
    history.insert(key, RESTORE_TYPE, RESTORE_STATUS);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setString(2, CLUSTER_ID);
    verify(insert).setString(3, TARGET_IP);
    verify(insert).setInt(4, RESTORE_TYPE.get());
    verify(insert).setLong(5, CREATED_AT);
    verify(insert).setInt(7, RESTORE_STATUS.getNumber());
    verify(insert).executeUpdate();
    verify(insert).clearParameters();
  }

  @Test
  public void insert_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(insert.executeUpdate()).thenThrow(toThrow);
    doNothing().when(insert).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .createdAt(CREATED_AT)
            .build();

    // Act Assert
    assertThatThrownBy(
            () -> {
              history.insert(key, RESTORE_TYPE, RESTORE_STATUS);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setString(2, CLUSTER_ID);
    verify(insert).setString(3, TARGET_IP);
    verify(insert).setInt(4, RESTORE_TYPE.get());
    verify(insert).setLong(5, CREATED_AT);
    verify(insert).setInt(7, RESTORE_STATUS.getNumber());
    verify(insert).executeUpdate();
    verify(insert).clearParameters();
  }

  @Test
  public void update_ProperArgumentsGiven_ShouldExecuteProperly() throws SQLException {
    // Arrange
    when(update.executeUpdate()).thenReturn(1);
    doNothing().when(update).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .createdAt(CREATED_AT)
            .build();

    // Act
    history.update(key, RESTORE_STATUS);

    // Assert
    verify(update).setInt(1, RESTORE_STATUS.getNumber());
    verify(update).setString(3, SNAPSHOT_ID);
    verify(update).setString(4, CLUSTER_ID);
    verify(update).setString(5, TARGET_IP);
    verify(update).setLong(6, CREATED_AT);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void update_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(update.executeUpdate()).thenThrow(toThrow);
    doNothing().when(update).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .createdAt(CREATED_AT)
            .build();

    // Act Assert
    assertThatThrownBy(
            () -> {
              history.update(key, RESTORE_STATUS);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(update).setInt(1, RESTORE_STATUS.getNumber());
    verify(update).setString(3, SNAPSHOT_ID);
    verify(update).setString(4, CLUSTER_ID);
    verify(update).setString(5, TARGET_IP);
    verify(update).setLong(6, CREATED_AT);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void selectRecent_RestoreStatusListingRequestWithClusterGiven_ShouldReturnStatusProperly()
      throws SQLException {
    // Arrange
    RestoreStatusListingRequest request =
        RestoreStatusListingRequest.newBuilder().setClusterId(CLUSTER_ID).build();
    ResultSet resultSet = mock(ResultSet.class);
    when(selectRecentByCluster.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(false);

    // Act
    history.selectRecent(request);

    // Assert
    verify(selectRecentByCluster).clearParameters();
    verify(selectRecentByCluster).setString(1, CLUSTER_ID);
    verify(selectRecentByCluster).executeQuery();
  }

  @Test
  public void selectRecent_SQLExceptionThrown_ShouldThrowDatabaseException() throws SQLException {
    // Arrange
    RestoreStatusListingRequest request =
        RestoreStatusListingRequest.newBuilder().setClusterId(CLUSTER_ID).build();
    SQLException toThrow = mock(SQLException.class);
    when(selectRecentByCluster.executeQuery()).thenThrow(toThrow);
    doNothing().when(selectRecentByCluster).clearParameters();

    // Act
    assertThatThrownBy(
            () -> {
              history.selectRecent(request);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(selectRecentByCluster).clearParameters();
    verify(selectRecentByCluster).setString(1, CLUSTER_ID);
    verify(selectRecentByCluster).executeQuery();
  }

  @Test
  public void selectRecent_RestoreStatusListingRequestWithTargetIpGiven_ShouldReturnStatusProperly()
      throws SQLException {
    // Arrange
    RestoreStatusListingRequest request =
        RestoreStatusListingRequest.newBuilder()
            .setClusterId(CLUSTER_ID)
            .setTargetIp(TARGET_IP)
            .setN(N)
            .build();
    ResultSet resultSet = mock(ResultSet.class);
    when(selectRecentByHost.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<RestoreHistoryRecord> records = history.selectRecent(request);

    // Assert
    verify(selectRecentByHost).clearParameters();
    verify(selectRecentByHost).setString(1, CLUSTER_ID);
    verify(selectRecentByHost).setString(2, TARGET_IP);
    verify(selectRecentByHost).setInt(3, N);
    verify(selectRecentByHost).executeQuery();
    assertThat(records.size()).isEqualTo(N);
  }

  @Test
  public void
      selectRecent_RestoreStatusListingRequestWithSnapshotIdGiven_ShouldReturnSnapshotsProperly()
          throws SQLException {
    // Arrange
    RestoreStatusListingRequest request =
        RestoreStatusListingRequest.newBuilder().setSnapshotId(SNAPSHOT_ID).setN(N).build();
    ResultSet resultSet = mock(ResultSet.class);
    when(selectBySnapshot.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<RestoreHistoryRecord> records = history.selectRecent(request);

    // Assert
    verify(selectBySnapshot).setString(1, SNAPSHOT_ID);
    verify(selectBySnapshot).setInt(2, N);
    verify(selectBySnapshot).executeQuery();
    assertThat(records.size()).isEqualTo(N);
  }
}
