package com.scalar.backup.cassandra.db;

import com.scalar.backup.cassandra.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ClusterInfo {
  private static final String DELIMITER = ",";
  static final String INSERT =
      "INSERT INTO cluster_info "
          + "(cluster_id, target_ips, keyspaces, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
  static final String UPDATE =
      "UPDATE cluster_info SET target_ips = ?, keyspaces = ?, updated_at = ? WHERE cluster_id = ?";
  static final String SELECT_BY_CLUSTER = "SELECT * FROM cluster_info WHERE cluster_id = ?";
  static final String SELECT_RECENT = "SELECT * FROM cluster_info ORDER BY created_at DESC limit ?";
  private final Connection connection;
  private final PreparedStatement insert;
  private final PreparedStatement update;
  private final PreparedStatement selectByCluster;
  private final PreparedStatement selectRecent;

  public ClusterInfo(Connection connection) {
    this.connection = connection;
    try {
      insert = connection.prepareStatement(INSERT);
      insert.setQueryTimeout(30);
      update = connection.prepareStatement(UPDATE);
      update.setQueryTimeout(30);
      selectByCluster = connection.prepareStatement(SELECT_BY_CLUSTER);
      selectByCluster.setQueryTimeout(30);
      selectRecent = connection.prepareStatement(SELECT_RECENT);
      selectRecent.setQueryTimeout(30);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void upsert(String clusterId, List<String> targetIps, List<String> keyspaces) {
    ClusterInfoRecord record = selectByClusterId(clusterId);
    if (record.getClusterId().isEmpty()) {
      insert(clusterId, targetIps, keyspaces);
    } else {
      update(clusterId, targetIps, keyspaces);
    }
  }

  public void insert(String clusterId, List<String> targetIps, List<String> keyspaces) {
    try {
      insertImpl(clusterId, targetIps, keyspaces);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void update(String clusterId, List<String> targetIps, List<String> keyspaces) {
    try {
      updateImpl(clusterId, targetIps, keyspaces);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public ClusterInfoRecord selectByClusterId(String clusterId) {
    List<ClusterInfoRecord> records;
    try {
      selectByCluster.setString(1, clusterId);
      ResultSet resultSet = selectByCluster.executeQuery();
      records = traverseResults(resultSet);
      selectByCluster.clearParameters();
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }

    if (records.isEmpty()) {
      return ClusterInfoRecord.newBuilder().build();
    }
    return records.get(0);
  }

  public List<ClusterInfoRecord> selectRecent(int n) {
    List<ClusterInfoRecord> records;
    try {
      selectRecent.setInt(1, n);
      ResultSet resultSet = selectRecent.executeQuery();
      records = traverseResults(resultSet);
      selectRecent.clearParameters();
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }

    return records;
  }

  private void insertImpl(String clusterId, List<String> targetIps, List<String> keyspaces)
      throws SQLException {
    insert.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    insert.setString(1, clusterId);
    insert.setString(2, String.join(DELIMITER, targetIps));
    insert.setString(3, String.join(DELIMITER, keyspaces));
    insert.setLong(4, currentTimestamp);
    insert.setLong(5, currentTimestamp);
    if (insert.executeUpdate() != 1) {
      throw new SQLException("Inserting the record failed.");
    }
  }

  private void updateImpl(String clusterId, List<String> targetIps, List<String> keyspaces)
      throws SQLException {
    update.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    update.setString(1, String.join(DELIMITER, targetIps));
    update.setString(2, String.join(DELIMITER, keyspaces));
    update.setLong(3, currentTimestamp);
    update.setString(4, clusterId);
    if (update.executeUpdate() != 1) {
      throw new SQLException("Inserting the record failed.");
    }
  }

  private List<ClusterInfoRecord> traverseResults(ResultSet resultSet) {
    List<ClusterInfoRecord> records = new ArrayList<>();
    try {
      while (resultSet.next()) {
        ClusterInfoRecord.Builder builder = ClusterInfoRecord.newBuilder();
        builder.clusterId(resultSet.getString("cluster_id"));
        builder.targetIps(Arrays.asList(resultSet.getString("target_ips").split(DELIMITER)));
        builder.keyspaces(Arrays.asList(resultSet.getString("keyspaces").split(DELIMITER)));
        builder.createdAt(resultSet.getLong("created_at"));
        builder.updatedAt(resultSet.getLong("updated_at"));
        records.add(builder.build());
      }
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
    return records;
  }
}
