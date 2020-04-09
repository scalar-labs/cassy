package com.scalar.cassy.db;

import com.google.inject.Inject;
import com.scalar.cassy.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ClusterInfo {
  private static final String DELIMITER = ",";
  static final String INSERT =
      "INSERT INTO cluster_info "
          + "(cluster_id, cluster_name, target_ips, keyspaces, data_dir, created_at, updated_at) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
  static final String SELECT_BY_CLUSTER = "SELECT * FROM cluster_info WHERE cluster_id = ?";
  static final String SELECT_RECENT = "SELECT * FROM cluster_info ORDER BY created_at DESC limit ?";
  private final Connection connection;
  private final PreparedStatement insert;
  private final PreparedStatement selectByCluster;
  private final PreparedStatement selectRecent;

  @Inject
  public ClusterInfo(Connection connection) {
    this.connection = connection;
    try {
      insert = connection.prepareStatement(INSERT);
      insert.setQueryTimeout(30);
      selectByCluster = connection.prepareStatement(SELECT_BY_CLUSTER);
      selectByCluster.setQueryTimeout(30);
      selectRecent = connection.prepareStatement(SELECT_RECENT);
      selectRecent.setQueryTimeout(30);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public void insert(
      String clusterId,
      String clusterName,
      List<String> targetIps,
      List<String> keyspaces,
      String dataDir) {
    try {
      insertImpl(clusterId, clusterName, targetIps, keyspaces, dataDir);
    } catch (SQLException e) {
      throw new DatabaseException(e);
    }
  }

  public Optional<ClusterInfoRecord> selectByClusterId(String clusterId) {
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
      return Optional.empty();
    }
    return Optional.of(records.get(0));
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

  private void insertImpl(
      String clusterId,
      String clusterName,
      List<String> targetIps,
      List<String> keyspaces,
      String dataDir)
      throws SQLException {
    insert.clearParameters();
    long currentTimestamp = System.currentTimeMillis();
    insert.setString(1, clusterId);
    insert.setString(2, clusterName);
    insert.setString(3, String.join(DELIMITER, targetIps));
    insert.setString(4, String.join(DELIMITER, keyspaces));
    insert.setString(5, dataDir);
    insert.setLong(6, currentTimestamp);
    insert.setLong(7, currentTimestamp);
    if (insert.executeUpdate() != 1) {
      throw new SQLException("Inserting the record failed.");
    }
  }

  private List<ClusterInfoRecord> traverseResults(ResultSet resultSet) {
    List<ClusterInfoRecord> records = new ArrayList<>();
    try {
      while (resultSet.next()) {
        ClusterInfoRecord.Builder builder = ClusterInfoRecord.newBuilder();
        builder.clusterId(resultSet.getString("cluster_id"));
        builder.clusterName(resultSet.getString("cluster_name"));
        builder.targetIps(Arrays.asList(resultSet.getString("target_ips").split(DELIMITER)));
        builder.keyspaces(Arrays.asList(resultSet.getString("keyspaces").split(DELIMITER)));
        builder.dataDir(resultSet.getString("data_dir"));
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
