package com.scalar.backup.cassandra.db;

import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.exception.DatabaseException;
import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.service.BackupKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class BackupHistory {
  static final String INSERT =
      "INSERT INTO backup_history "
          + "(snapshot_id, incremental_id, cluster_id, target_ip, backup_type, created_at, updated_at, status) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
  static final String UPDATE =
      "UPDATE backup_history SET status = ?, updated_at = ? "
          + "WHERE snapshot_id = ? and incremental_id = ? and cluster_id = ? and target_ip = ?";
  static final String SELECT_RECENT_SNAPSHOTS_BY_CLUSTER =
      "SELECT * FROM backup_history WHERE cluster_id = ? "
          + "GROUP BY snapshot_id ORDER BY incremental_id DESC limit ?";
  static final String SELECT_RECENT_SNAPSHOTS_BY_HOST =
      "SELECT * FROM backup_history WHERE cluster_id = ? and target_ip = ? "
          + "GROUP BY snapshot_id ORDER BY incremental_id DESC limit ?";
  static final String SELECT_BY_SNAPSHOT_ID =
      "SELECT * FROM backup_history WHERE snapshot_id = ? ORDER BY incremental_id DESC";
  private final Connection connection;
  private final PreparedStatement insert;
  private final PreparedStatement update;
  private final PreparedStatement selectRecentByCluster;
  private final PreparedStatement selectRecentByHost;
  private final PreparedStatement selectBySnapshot;

  public BackupHistory(Connection connection) {
    this.connection = connection;
    try {
      insert = connection.prepareStatement(INSERT);
      update = connection.prepareStatement(UPDATE);
      selectRecentByCluster = connection.prepareStatement(SELECT_RECENT_SNAPSHOTS_BY_CLUSTER);
      selectRecentByHost = connection.prepareStatement(SELECT_RECENT_SNAPSHOTS_BY_HOST);
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
      if (request.getTargetIp().isEmpty()) {
        resultSet = selectRecentSnapshotsByCluster(request);
      } else {
        resultSet = selectRecentSnapshotsByHost(request);
      }
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }

    return traverseResults(resultSet);
  }

  public List<BackupHistoryRecord> selectBySnapshotId(String snapshotId) {
    List<BackupHistoryRecord> records;
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

  private void insertImpl(BackupKey backupKey, BackupType type, OperationStatus status)
      throws SQLException {
    insert.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    insert.setString(1, backupKey.getSnapshotId());
    insert.setLong(2, backupKey.getIncrementalId());
    insert.setString(3, backupKey.getClusterId());
    insert.setString(4, backupKey.getTargetIp());
    insert.setInt(5, type.get());
    insert.setLong(6, currentTimestamp);
    insert.setLong(7, currentTimestamp);
    insert.setInt(8, status.getNumber());
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
    update.setLong(4, backupKey.getIncrementalId());
    update.setString(5, backupKey.getClusterId());
    update.setString(6, backupKey.getTargetIp());

    if (update.executeUpdate() != 1) {
      throw new SQLException("Updating the record failed.");
    }
  }

  private ResultSet selectRecentSnapshotsByCluster(BackupListingRequest request)
      throws SQLException {
    selectRecentByCluster.clearParameters();
    selectRecentByCluster.setString(1, request.getClusterId());
    selectRecentByCluster.setInt(2, request.getN());
    ResultSet resultSet = selectRecentByCluster.executeQuery();
    return resultSet;
  }

  private ResultSet selectRecentSnapshotsByHost(BackupListingRequest request) throws SQLException {
    selectRecentByHost.clearParameters();
    selectRecentByHost.setString(1, request.getClusterId());
    selectRecentByHost.setString(2, request.getTargetIp());
    selectRecentByHost.setInt(3, request.getN());
    ResultSet resultSet = selectRecentByHost.executeQuery();
    return resultSet;
  }

  private List<BackupHistoryRecord> traverseResults(ResultSet resultSet) {
    List<BackupHistoryRecord> records = new ArrayList<>();
    try {
      while (resultSet.next()) {
        BackupHistoryRecord.Builder builder = BackupHistoryRecord.newBuilder();
        builder.snapshotId(resultSet.getString("snapshot_id"));
        builder.incrementalId(resultSet.getLong("incremental_id"));
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
