package com.scalar.backup.cassandra.db;

import com.scalar.backup.cassandra.rpc.BackupListingRequest;
import com.scalar.backup.cassandra.rpc.BackupRequest;
import com.scalar.backup.cassandra.rpc.BackupStatus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BackupHistoryTmpTest {
  public static void main(String[] args) {
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:cassandra_backup.db")) {
      BackupHistory history = new BackupHistory(connection);

      // insert(history);
      // update(history);
      select(history);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void insert(BackupHistory history) {
    BackupRequest request =
        BackupRequest.newBuilder()
            .setClusterId("Test Cluster")
            .setTargetIp("192.168.1.1")
            .setBackupType(2)
            .build();
    history.insert(request, "xxx", 20190626, BackupStatus.STARTED);
    history.insert(request, "yyy", 20190626, BackupStatus.STARTED);
  }

  private static void update(BackupHistory history) {
    BackupRequest request =
        BackupRequest.newBuilder()
            .setClusterId("Test Cluster")
            .setTargetIp("192.168.1.1")
            .setBackupType(2)
            .build();
    history.update(request, "xxx", 20190626, BackupStatus.COMPLETED);
    history.update(request, "yyy", 20190626, BackupStatus.COMPLETED);
  }

  private static void select(BackupHistory history) {
    List<BackupHistoryRecord> records;
    BackupListingRequest request =
        BackupListingRequest.newBuilder()
            .setClusterId("Test Cluster")
            .setTargetIp("192.168.1.1")
            .setN(10)
            .build();
    records = history.selectRecentSnapshots(request);
    System.out.println(records);
    records = history.selectBySnapshotId("xxx");
    System.out.println(records);
    records = history.selectBySnapshotId("yyy");
    System.out.println(records);
  }
}
