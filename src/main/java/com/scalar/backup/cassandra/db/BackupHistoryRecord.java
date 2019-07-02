package com.scalar.backup.cassandra.db;

import static com.google.common.base.Preconditions.checkArgument;

import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.rpc.BackupStatus;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BackupHistoryRecord {
  private final String snapshotId;
  private final long backupId;
  private final String clusterId;
  private final String targetIp;
  private final BackupType backupType;
  private final long createdAt;
  private final long updatedAt;
  private final BackupStatus status;

  private BackupHistoryRecord(Builder builder) {
    this.snapshotId = builder.snapshotId;
    this.backupId = builder.backupId;
    this.clusterId = builder.clusterId;
    this.targetIp = builder.targetIp;
    this.backupType = builder.backupType;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
    this.status = builder.status;
  }

  public String getSnapshotId() {
    return snapshotId;
  }

  public long getBackupId() {
    return backupId;
  }

  public String getClusterId() {
    return clusterId;
  }

  public String getTargetIp() {
    return targetIp;
  }

  public BackupType getBackupType() {
    return backupType;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public BackupStatus getStatus() {
    return status;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String snapshotId;
    private long backupId;
    private String clusterId;
    private String targetIp;
    private BackupType backupType;
    private long createdAt;
    private long updatedAt;
    private BackupStatus status;

    public Builder snapshotId(String snapshotId) {
      checkArgument(snapshotId != null);
      this.snapshotId = snapshotId;
      return this;
    }

    public Builder backupId(long backupId) {
      checkArgument(backupId != 0L);
      this.backupId = backupId;
      return this;
    }

    public Builder clusterId(String clusterId) {
      checkArgument(clusterId != null);
      this.clusterId = clusterId;
      return this;
    }

    public Builder targetIp(String targetIp) {
      checkArgument(targetIp != null);
      this.targetIp = targetIp;
      return this;
    }

    public Builder backupType(BackupType backupType) {
      checkArgument(backupType != null);
      this.backupType = backupType;
      return this;
    }

    public Builder createdAt(long createdAt) {
      checkArgument(createdAt != 0L);
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(long updatedAt) {
      checkArgument(updatedAt != 0L);
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder status(BackupStatus status) {
      checkArgument(status != null);
      this.status = status;
      return this;
    }

    public BackupHistoryRecord build() {
      if (snapshotId == null
          || backupId == 0L
          || clusterId == null
          || targetIp == null
          || backupType == null
          || createdAt == 0L
          || updatedAt == 0L
          || status == null) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new BackupHistoryRecord(this);
    }
  }
}
