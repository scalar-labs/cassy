package com.scalar.backup.cassandra.db;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ClusterInfoRecord {
  private final String clusterId;
  private final List<String> targetIps;
  private final List<String> keyspaces;
  private final long createdAt;
  private final long updatedAt;

  private ClusterInfoRecord(Builder builder) {
    this.clusterId = builder.clusterId;
    this.targetIps = builder.targetIps;
    this.keyspaces = builder.keyspaces;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  public String getClusterId() {
    return clusterId;
  }

  public List<String> getTargetIps() {
    return ImmutableList.copyOf(targetIps);
  }

  public List<String> getKeyspaces() {
    return ImmutableList.copyOf(keyspaces);
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String clusterId;
    private List<String> targetIps;
    private List<String> keyspaces;
    private long createdAt;
    private long updatedAt;

    public Builder clusterId(String clusterId) {
      checkArgument(clusterId != null);
      this.clusterId = clusterId;
      return this;
    }

    public Builder targetIps(List<String> targetIps) {
      checkArgument(targetIps != null);
      this.targetIps = targetIps;
      return this;
    }

    public Builder keyspaces(List<String> keyspaces) {
      checkArgument(keyspaces != null);
      this.keyspaces = keyspaces;
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

    public ClusterInfoRecord build() {
      if (clusterId == null
          || targetIps == null
          || keyspaces == null
          || createdAt == 0L
          || updatedAt == 0L) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new ClusterInfoRecord(this);
    }
  }
}
