package com.scalar.cassy.db;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.DatabaseException;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.service.BackupKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class BackupHistory {
  static final String INSERT =
      "INSERT INTO backup_history "
          + "(snapshot_id, cluster_id, target_ip, backup_type, created_at, updated_at, status) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
  static final String UPDATE =
      "UPDATE backup_history SET status = ?, updated_at = ? "
          + "WHERE snapshot_id = ? and cluster_id = ? and target_ip = ? and created_at = ?";
  static final String SELECT_RECENT_SNAPSHOTS_BY_CLUSTER =
      "SELECT * FROM backup_history WHERE cluster_id = ? ORDER BY created_at DESC limit ?";
  static final String SELECT_RECENT_SNAPSHOTS_BY_HOST =
      "SELECT * FROM backup_history WHERE cluster_id = ? and target_ip = ? "
          + "ORDER BY created_at DESC limit ?";
  static final String SELECT_RECENT_BY_SNAPSHOT_ID =
      "SELECT * FROM backup_history WHERE snapshot_id = ? ORDER BY created_at DESC limit ?";
  private static final int DEFAULT_N = -1;
  private final Connection connection;
  private final PreparedStatement insert;
  private final PreparedStatement update;
  private final PreparedStatement selectRecentByCluster;
  private final PreparedStatement selectRecentByHost;
  private final PreparedStatement selectRecentBySnapshot;

  public BackupHistory(Connection connection) {
    this.connection = connection;
    try {
      insert = connection.prepareStatement(INSERT);
      update = connection.prepareStatement(UPDATE);
      selectRecentByCluster = connection.prepareStatement(SELECT_RECENT_SNAPSHOTS_BY_CLUSTER);
      selectRecentByHost = connection.prepareStatement(SELECT_RECENT_SNAPSHOTS_BY_HOST);
      selectRecentBySnapshot = connection.prepareStatement(SELECT_RECENT_BY_SNAPSHOT_ID);
      insert.setQueryTimeout(30);
      update.setQueryTimeout(30);
      selectRecentByCluster.setQueryTimeout(30);
      selectRecentByHost.setQueryTimeout(30);
      selectRecentBySnapshot.setQueryTimeout(30);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void insert(BackupKey backupKey, BackupType type, OperationStatus status) {
    try {
      insertImpl(backupKey, type, status);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void update(BackupKey backupKey, OperationStatus status) {
    try {
      updateImpl(backupKey, status);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public List<BackupHistoryRecord> selectRecentSnapshots(BackupListingRequest request) {
    ResultSet resultSet;
    try {
      if (!request.getTargetIp().isEmpty() && !request.getClusterId().isEmpty()) {
        resultSet = selectRecentSnapshotsByHost(request);
      } else if (!request.getSnapshotId().isEmpty()) {
        resultSet = selectRecentBySnapshotId(request);
      } else if (!request.getClusterId().isEmpty()) {
        resultSet = selectRecentSnapshotsByCluster(request);
      } else {
        throw new IllegalArgumentException("Parameters are not set properly.");
      }
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }

    return traverseResults(resultSet);
  }

  private void insertImpl(BackupKey backupKey, BackupType type, OperationStatus status)
      throws SQLException {
    insert.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    insert.setString(1, backupKey.getSnapshotId());
    insert.setString(2, backupKey.getClusterId());
    insert.setString(3, backupKey.getTargetIp());
    insert.setInt(4, type.get());
    insert.setLong(5, backupKey.getCreatedAt());
    insert.setLong(6, currentTimestamp);
    insert.setInt(7, status.getNumber());
    if (insert.executeUpdate() != 1) {
      throw new SQLException("Inserting the record failed.");
    }
  }

  private void updateImpl(BackupKey backupKey, OperationStatus status) throws SQLException {
    update.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    update.setInt(1, status.getNumber());
    update.setLong(2, currentTimestamp);
    update.setString(3, backupKey.getSnapshotId());
    update.setString(4, backupKey.getClusterId());
    update.setString(5, backupKey.getTargetIp());
    update.setLong(6, backupKey.getCreatedAt());
    if (update.executeUpdate() != 1) {
      throw new SQLException("Updating the record failed.");
    }
  }

  private ResultSet selectRecentBySnapshotId(BackupListingRequest request) throws SQLException {
    selectRecentBySnapshot.setString(1, request.getSnapshotId());
    int n = request.getLimit() == 0 ? DEFAULT_N : request.getLimit();
    selectRecentBySnapshot.setInt(2, n);
    ResultSet resultSet = selectRecentBySnapshot.executeQuery();
    return resultSet;
  }

  private ResultSet selectRecentSnapshotsByCluster(BackupListingRequest request)
      throws SQLException {
    selectRecentByCluster.clearParameters();
    selectRecentByCluster.setString(1, request.getClusterId());
    int n = request.getLimit() == 0 ? DEFAULT_N : request.getLimit();
    selectRecentByCluster.setInt(2, n);
    ResultSet resultSet = selectRecentByCluster.executeQuery();
    return resultSet;
  }

  private ResultSet selectRecentSnapshotsByHost(BackupListingRequest request) throws SQLException {
    selectRecentByHost.clearParameters();
    selectRecentByHost.setString(1, request.getClusterId());
    selectRecentByHost.setString(2, request.getTargetIp());
    int n = request.getLimit() == 0 ? DEFAULT_N : request.getLimit();
    selectRecentByHost.setInt(3, n);
    ResultSet resultSet = selectRecentByHost.executeQuery();
    return resultSet;
  }

  private List<BackupHistoryRecord> traverseResults(ResultSet resultSet) {
    List<BackupHistoryRecord> records = new ArrayList<>();
    try {
      while (resultSet.next()) {
        BackupHistoryRecord.Builder builder = BackupHistoryRecord.newBuilder();
        builder.snapshotId(resultSet.getString("snapshot_id"));
        builder.clusterId(resultSet.getString("cluster_id"));
        builder.targetIp(resultSet.getString("target_ip"));
        builder.backupType(BackupType.getByType(resultSet.getInt("backup_type")));
        builder.createdAt(resultSet.getLong("created_at"));
        builder.updatedAt(resultSet.getLong("updated_at"));
        builder.status(OperationStatus.forNumber(resultSet.getInt("status")));
        records.add(builder.build());
      }
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
    return records;
  }
}
