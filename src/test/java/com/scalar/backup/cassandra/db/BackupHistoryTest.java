package com.scalar.backup.cassandra.db;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.exception.StatusDatabaseException;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.OperationStatus;
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

public class BackupHistoryTest {
  private final String CLUSTER_ID = "cluster_id";
  private final String TARGET_IP = "127.0.0.1";
  private final BackupType BACKUP_TYPE = BackupType.NODE_SNAPSHOT;
  private final String SNAPSHOT_ID = "snapshot_id";
  private final long INCREMENTAL_ID = 1L;
  private final OperationStatus BACKUP_STATUS = OperationStatus.COMPLETED;
  private final int N = 3;
  @Mock private Connection connection;
  @Mock private PreparedStatement insert;
  @Mock private PreparedStatement update;
  @Mock private PreparedStatement selectRecentByCluster;
  @Mock private PreparedStatement selectRecentByHost;
  @Mock private PreparedStatement selectBySnapshot;
  private BackupHistory history;

  @Before
  public void setUp() throws SQLException {
    MockitoAnnotations.initMocks(this);

    when(connection.prepareStatement(BackupHistory.INSERT)).thenReturn(insert);
    when(connection.prepareStatement(BackupHistory.UPDATE)).thenReturn(update);
    when(connection.prepareStatement(BackupHistory.SELECT_RECENT_SNAPSHOTS_BY_CLUSTER))
        .thenReturn(selectRecentByCluster);
    when(connection.prepareStatement(BackupHistory.SELECT_RECENT_SNAPSHOTS_BY_HOST))
        .thenReturn(selectRecentByHost);
    when(connection.prepareStatement(BackupHistory.SELECT_BY_SNAPSHOT_ID))
        .thenReturn(selectBySnapshot);
    doNothing().when(insert).setQueryTimeout(anyInt());
    doNothing().when(update).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByCluster).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByHost).setQueryTimeout(anyInt());
    doNothing().when(selectBySnapshot).setQueryTimeout(anyInt());

    // InjectMocks fails due to some dependency issue
    history = new BackupHistory(connection);
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
            .incrementalId(INCREMENTAL_ID)
            .build();

    // Act
    history.insert(key, BACKUP_TYPE, BACKUP_STATUS);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setLong(2, INCREMENTAL_ID);
    verify(insert).setString(3, CLUSTER_ID);
    verify(insert).setString(4, TARGET_IP);
    verify(insert).setInt(5, BACKUP_TYPE.get());
    verify(insert).setInt(8, BACKUP_STATUS.getNumber());
    verify(insert).executeUpdate();
    verify(insert).clearParameters();
  }

  @Test
  public void insert_SQLExceptionThrown_ShouldThrowStatusDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(insert.executeUpdate()).thenThrow(toThrow);
    doNothing().when(insert).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .incrementalId(INCREMENTAL_ID)
            .build();

    // Act Assert
    assertThatThrownBy(
            () -> {
              history.insert(key, BACKUP_TYPE, BACKUP_STATUS);
            })
        .isInstanceOf(StatusDatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setLong(2, INCREMENTAL_ID);
    verify(insert).setString(3, CLUSTER_ID);
    verify(insert).setString(4, TARGET_IP);
    verify(insert).setInt(5, BACKUP_TYPE.get());
    verify(insert).setInt(8, BACKUP_STATUS.getNumber());
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
            .incrementalId(INCREMENTAL_ID)
            .build();

    // Act
    history.update(key, BACKUP_STATUS);

    // Assert
    verify(update).setInt(1, BACKUP_STATUS.getNumber());
    verify(update).setString(3, SNAPSHOT_ID);
    verify(update).setLong(4, INCREMENTAL_ID);
    verify(update).setString(5, CLUSTER_ID);
    verify(update).setString(6, TARGET_IP);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void update_SQLExceptionThrown_ShouldThrowStatusDatabaseException() throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(update.executeUpdate()).thenThrow(toThrow);
    doNothing().when(update).clearParameters();
    BackupKey key =
        BackupKey.newBuilder()
            .clusterId(CLUSTER_ID)
            .targetIp(TARGET_IP)
            .snapshotId(SNAPSHOT_ID)
            .incrementalId(INCREMENTAL_ID)
            .build();

    // Act Assert
    assertThatThrownBy(
            () -> {
              history.update(key, BACKUP_STATUS);
            })
        .isInstanceOf(StatusDatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(update).setInt(1, BACKUP_STATUS.getNumber());
    verify(update).setString(3, SNAPSHOT_ID);
    verify(update).setLong(4, INCREMENTAL_ID);
    verify(update).setString(5, CLUSTER_ID);
    verify(update).setString(6, TARGET_IP);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void
      selectRecentSnapshots_BackupListingRequestWithClusterGiven_ShouldReturnSnapshotsProperly()
          throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setClusterId(CLUSTER_ID).setN(N).build();
    ResultSet resultSet = mock(ResultSet.class);
    when(selectRecentByCluster.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<BackupHistoryRecord> records = history.selectRecentSnapshots(request);

    // Assert
    verify(selectRecentByCluster).clearParameters();
    verify(selectRecentByCluster).setString(1, CLUSTER_ID);
    verify(selectRecentByCluster).setInt(2, N);
    verify(selectRecentByCluster).executeQuery();
    assertThat(records.size()).isEqualTo(N);
  }

  @Test
  public void selectRecentSnapshots_SQLExceptionThrown_ShouldThrowStatusDatabaseException()
      throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setClusterId(CLUSTER_ID).setN(N).build();
    SQLException toThrow = mock(SQLException.class);
    when(selectRecentByCluster.executeQuery()).thenThrow(toThrow);
    doNothing().when(selectRecentByCluster).clearParameters();

    // Act
    assertThatThrownBy(
            () -> {
              history.selectRecentSnapshots(request);
            })
        .isInstanceOf(StatusDatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(selectRecentByCluster).clearParameters();
    verify(selectRecentByCluster).setString(1, CLUSTER_ID);
    verify(selectRecentByCluster).setInt(2, N);
    verify(selectRecentByCluster).executeQuery();
  }

  @Test
  public void
      selectRecentSnapshots_BackupListingRequestWithTargetIpGiven_ShouldReturnSnapshotsProperly()
          throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder()
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
    List<BackupHistoryRecord> records = history.selectRecentSnapshots(request);

    // Assert
    verify(selectRecentByHost).clearParameters();
    verify(selectRecentByHost).setString(1, CLUSTER_ID);
    verify(selectRecentByHost).setString(2, TARGET_IP);
    verify(selectRecentByHost).setInt(3, N);
    verify(selectRecentByHost).executeQuery();
    assertThat(records.size()).isEqualTo(N);
  }

  @Test
  public void selectBySnapshotId_SnapshotIdGiven_ShouldReturnSnapshotsProperly()
      throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(selectBySnapshot.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<BackupHistoryRecord> records = history.selectBySnapshotId(SNAPSHOT_ID);

    // Assert
    verify(selectBySnapshot).setString(1, SNAPSHOT_ID);
    verify(selectBySnapshot).executeQuery();
    assertThat(records.size()).isEqualTo(N);
  }

  @Test
  public void selectBySnapshotId_SQLExceptionThrown_ShouldThrowStatusDatabaseException()
      throws SQLException {
    // Arrange
    SQLException toThrow = mock(SQLException.class);
    when(selectBySnapshot.executeQuery()).thenThrow(toThrow);

    // Act
    assertThatThrownBy(
            () -> {
              history.selectBySnapshotId(SNAPSHOT_ID);
            })
        .isInstanceOf(StatusDatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(selectBySnapshot).setString(1, SNAPSHOT_ID);
    verify(selectBySnapshot).executeQuery();
  }
}
