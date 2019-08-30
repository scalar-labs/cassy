package com.scalar.cassy.db;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.DatabaseException;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.service.BackupKey;
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
  private final long CREATED_AT = 1L;
  private final OperationStatus BACKUP_STATUS = OperationStatus.COMPLETED;
  private final int LIMIT = 3;
  @Mock private Connection connection;
  @Mock private PreparedStatement insert;
  @Mock private PreparedStatement update;
  @Mock private PreparedStatement selectRecentByCluster;
  @Mock private PreparedStatement selectRecentByHost;
  @Mock private PreparedStatement selectRecentBySnapshot;
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
    when(connection.prepareStatement(BackupHistory.SELECT_RECENT_BY_SNAPSHOT_ID))
        .thenReturn(selectRecentBySnapshot);
    doNothing().when(insert).setQueryTimeout(anyInt());
    doNothing().when(update).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByCluster).setQueryTimeout(anyInt());
    doNothing().when(selectRecentByHost).setQueryTimeout(anyInt());
    doNothing().when(selectRecentBySnapshot).setQueryTimeout(anyInt());

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
            .createdAt(CREATED_AT)
            .build();

    // Act
    history.insert(key, BACKUP_TYPE, BACKUP_STATUS);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setString(2, CLUSTER_ID);
    verify(insert).setString(3, TARGET_IP);
    verify(insert).setInt(4, BACKUP_TYPE.get());
    verify(insert).setLong(5, CREATED_AT);
    verify(insert).setInt(7, BACKUP_STATUS.getNumber());
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
              history.insert(key, BACKUP_TYPE, BACKUP_STATUS);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(insert).setString(1, SNAPSHOT_ID);
    verify(insert).setString(2, CLUSTER_ID);
    verify(insert).setString(3, TARGET_IP);
    verify(insert).setInt(4, BACKUP_TYPE.get());
    verify(insert).setLong(5, CREATED_AT);
    verify(insert).setInt(7, BACKUP_STATUS.getNumber());
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
    history.update(key, BACKUP_STATUS);

    // Assert
    verify(update).setInt(1, BACKUP_STATUS.getNumber());
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
              history.update(key, BACKUP_STATUS);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(update).setInt(1, BACKUP_STATUS.getNumber());
    verify(update).setString(3, SNAPSHOT_ID);
    verify(update).setString(4, CLUSTER_ID);
    verify(update).setString(5, TARGET_IP);
    verify(update).setLong(6, CREATED_AT);
    verify(update).executeUpdate();
    verify(update).clearParameters();
  }

  @Test
  public void
      selectRecentSnapshots_BackupListingRequestWithClusterGiven_ShouldReturnSnapshotsProperly()
          throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setClusterId(CLUSTER_ID).setLimit(LIMIT).build();
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
    verify(selectRecentByCluster).setInt(2, LIMIT);
    verify(selectRecentByCluster).executeQuery();
    assertThat(records.size()).isEqualTo(LIMIT);
  }

  @Test
  public void selectRecentSnapshots_SQLExceptionThrown_ShouldThrowDatabaseException()
      throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setClusterId(CLUSTER_ID).setLimit(LIMIT).build();
    SQLException toThrow = mock(SQLException.class);
    when(selectRecentByCluster.executeQuery()).thenThrow(toThrow);
    doNothing().when(selectRecentByCluster).clearParameters();

    // Act
    assertThatThrownBy(
            () -> {
              history.selectRecentSnapshots(request);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(selectRecentByCluster).clearParameters();
    verify(selectRecentByCluster).setString(1, CLUSTER_ID);
    verify(selectRecentByCluster).setInt(2, LIMIT);
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
            .setLimit(LIMIT)
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
    verify(selectRecentByHost).setInt(3, LIMIT);
    verify(selectRecentByHost).executeQuery();
    assertThat(records.size()).isEqualTo(LIMIT);
  }

  @Test
  public void selectRecentSnapshots_SnapshotIdGiven_ShouldReturnSnapshotsProperly()
      throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setSnapshotId(SNAPSHOT_ID).setLimit(LIMIT).build();
    ResultSet resultSet = mock(ResultSet.class);
    when(selectRecentBySnapshot.executeQuery()).thenReturn(resultSet);
    when(resultSet.getString(anyString())).thenReturn("anyString");
    when(resultSet.getLong(anyString())).thenReturn(1L);
    when(resultSet.getInt(anyString())).thenReturn(1);
    when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    // Act
    List<BackupHistoryRecord> records = history.selectRecentSnapshots(request);

    // Assert
    verify(selectRecentBySnapshot).setString(1, SNAPSHOT_ID);
    verify(selectRecentBySnapshot).setInt(2, LIMIT);
    verify(selectRecentBySnapshot).executeQuery();
    assertThat(records.size()).isEqualTo(LIMIT);
  }

  @Test
  public void
      selectRecentSnapshots_SnapshotIdGivenAndSQLExceptionThrown_ShouldThrowDatabaseException()
          throws SQLException {
    // Arrange
    BackupListingRequest request =
        BackupListingRequest.newBuilder().setSnapshotId(SNAPSHOT_ID).build();
    SQLException toThrow = mock(SQLException.class);
    when(selectRecentBySnapshot.executeQuery()).thenThrow(toThrow);

    // Act
    assertThatThrownBy(
            () -> {
              history.selectRecentSnapshots(request);
            })
        .isInstanceOf(DatabaseException.class)
        .hasCause(toThrow);

    // Assert
    verify(selectRecentBySnapshot).setString(1, SNAPSHOT_ID);
    verify(selectRecentBySnapshot).executeQuery();
  }
}
