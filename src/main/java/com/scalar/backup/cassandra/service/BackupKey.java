package com.scalar.backup.cassandra.service;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupKey {
  private final String snapshotId;
  private final String clusterId;
  private final String targetIp;
  private final long createdAt;

  private BackupKey(Builder builder) {
    this.snapshotId = builder.snapshotId;
    this.clusterId = builder.clusterId;
    this.targetIp = builder.targetIp;
    this.createdAt = builder.createdAt;
  }

  public String getSnapshotId() {
    return snapshotId;
  }

  public String getClusterId() {
    return clusterId;
  }

  public String getTargetIp() {
    return targetIp;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String snapshotId;
    private String clusterId;
    private String targetIp;
    private long createdAt;

    public Builder snapshotId(String snapshotId) {
      checkArgument(snapshotId != null);
      this.snapshotId = snapshotId;
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

    public Builder createdAt(long createdAt) {
      checkArgument(createdAt >= 0);
      this.createdAt = createdAt;
      return this;
    }

    public BackupKey build() {
      if (snapshotId == null || clusterId == null || targetIp == null || createdAt == 0) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new BackupKey(this);
    }
  }
}
