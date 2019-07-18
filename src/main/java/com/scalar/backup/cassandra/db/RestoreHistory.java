package com.scalar.backup.cassandra.db;

import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.exception.DatabaseException;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest;
import com.scalar.backup.cassandra.service.BackupKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class RestoreHistory {
  static final String INSERT =
      "INSERT INTO restore_history "
          + "(snapshot_id, cluster_id, target_ip, restore_type, created_at, updated_at, status) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
  static final String UPDATE =
      "UPDATE restore_history SET status = ?, updated_at = ? "
          + "WHERE snapshot_id = ? and cluster_id = ? and target_ip = ? and created_at = ?";
  static final String SELECT_RECENT_BY_CLUSTER =
      "SELECT cluster_id, target_ip, snapshot_id, MAX(created_at) as created_at,"
          + "updated_at, restore_type, status FROM restore_history "
          + "WHERE cluster_id = ? GROUP BY target_ip ORDER BY created_at DESC";
  static final String SELECT_RECENT_BY_HOST =
      "SELECT * FROM restore_history WHERE cluster_id = ? and target_ip = ? "
          + "ORDER BY created_at DESC limit ?";
  static final String SELECT_BY_SNAPSHOT_ID =
      "SELECT * FROM restore_history WHERE snapshot_id = ? ORDER BY created_at DESC limit ?";
  private final Connection connection;
  private final PreparedStatement insert;
  private final PreparedStatement update;
  private final PreparedStatement selectRecentByCluster;
  private final PreparedStatement selectRecentByHost;
  private final PreparedStatement selectBySnapshot;

  public RestoreHistory(Connection connection) {
    this.connection = connection;
    try {
      insert = connection.prepareStatement(INSERT);
      update = connection.prepareStatement(UPDATE);
      selectRecentByCluster = connection.prepareStatement(SELECT_RECENT_BY_CLUSTER);
      selectRecentByHost = connection.prepareStatement(SELECT_RECENT_BY_HOST);
      selectBySnapshot = connection.prepareStatement(SELECT_BY_SNAPSHOT_ID);
      insert.setQueryTimeout(30);
      update.setQueryTimeout(30);
      selectRecentByCluster.setQueryTimeout(30);
      selectRecentByHost.setQueryTimeout(30);
      selectBySnapshot.setQueryTimeout(30);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void insert(BackupKey backupKey, RestoreType type, OperationStatus status) {
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

  public List<RestoreHistoryRecord> selectRecent(RestoreStatusListingRequest request) {
    ResultSet resultSet;
    try {
      if (request.getTargetIp().isEmpty()) {
        resultSet = selectRecentByCluster(request);
      } else {
        resultSet = selectRecentByHost(request);
      }
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }

    return traverseResults(resultSet);
  }

  public List<RestoreHistoryRecord> selectBySnapshotId(String snapshotId) {
    List<RestoreHistoryRecord> records;
    try {
      selectBySnapshot.setString(1, snapshotId);
      ResultSet resultSet = selectBySnapshot.executeQuery();
      records = traverseResults(resultSet);
      selectBySnapshot.clearParameters();
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
    return records;
  }

  private void insertImpl(BackupKey backupKey, RestoreType type, OperationStatus status)
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

  private ResultSet selectRecentByCluster(RestoreStatusListingRequest request) throws SQLException {
    selectRecentByCluster.clearParameters();
    selectRecentByCluster.setString(1, request.getClusterId());
    ResultSet resultSet = selectRecentByCluster.executeQuery();
    return resultSet;
  }

  private ResultSet selectRecentByHost(RestoreStatusListingRequest request) throws SQLException {
    selectRecentByHost.clearParameters();
    selectRecentByHost.setString(1, request.getClusterId());
    selectRecentByHost.setString(2, request.getTargetIp());
    selectRecentByHost.setInt(3, request.getN());
    ResultSet resultSet = selectRecentByHost.executeQuery();
    return resultSet;
  }

  private List<RestoreHistoryRecord> traverseResults(ResultSet resultSet) {
    List<RestoreHistoryRecord> records = new ArrayList<>();
    try {
      while (resultSet.next()) {
        RestoreHistoryRecord.Builder builder = RestoreHistoryRecord.newBuilder();
        builder.snapshotId(resultSet.getString("snapshot_id"));
        builder.clusterId(resultSet.getString("cluster_id"));
        builder.targetIp(resultSet.getString("target_ip"));
        builder.restoreType(RestoreType.getByType(resultSet.getInt("restore_type")));
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
