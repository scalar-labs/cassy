package com.scalar.backup.cassandra.service;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupKey {
  private final String snapshotId;
  private final String clusterId;
  private final String targetIp;
  private final long incrementalId;

  private BackupKey(Builder builder) {
    this.snapshotId = builder.snapshotId;
    this.clusterId = builder.clusterId;
    this.targetIp = builder.targetIp;
    this.incrementalId = builder.incrementalId;
  }

  public String getSnapshotId() {
    return snapshotId;
  }

  public long getIncrementalId() {
    return incrementalId;
  }

  public String getClusterId() {
    return clusterId;
  }

  public String getTargetIp() {
    return targetIp;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String snapshotId;
    private String clusterId;
    private String targetIp;
    private long incrementalId;

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

    public Builder incrementalId(long incrementalId) {
      this.incrementalId = incrementalId;
      return this;
    }

    public BackupKey build() {
      if (snapshotId == null || clusterId == null || targetIp == null) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new BackupKey(this);
    }
  }
}
