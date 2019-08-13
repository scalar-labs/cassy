package com.scalar.backup.cassandra.db;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.concurrent.Immutable;

// Not final for mocking
@Immutable
public class ClusterInfoRecord {
  private final String clusterId;
  private final String clusterName;
  private final List<String> targetIps;
  private final List<String> keyspaces;
  private final String dataDir;
  private final long createdAt;
  private final long updatedAt;

  private ClusterInfoRecord(Builder builder) {
    this.clusterId = builder.clusterId;
    this.clusterName = builder.clusterName;
    this.targetIps = builder.targetIps;
    this.keyspaces = builder.keyspaces;
    this.dataDir = builder.dataDir;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  public String getClusterId() {
    return clusterId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public List<String> getTargetIps() {
    return ImmutableList.copyOf(targetIps);
  }

  public List<String> getKeyspaces() {
    return ImmutableList.copyOf(keyspaces);
  }

  public String getDataDir() {
    return dataDir;
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
    private String clusterName;
    private List<String> targetIps;
    private List<String> keyspaces;
    private String dataDir;
    private long createdAt;
    private long updatedAt;

    public Builder clusterId(String clusterId) {
      checkArgument(clusterId != null);
      this.clusterId = clusterId;
      return this;
    }

    public Builder clusterName(String clusterName) {
      checkArgument(clusterName != null);
      this.clusterName = clusterName;
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

    public Builder dataDir(String dataDir) {
      checkArgument(dataDir != null);
      this.dataDir = dataDir;
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
          || clusterName == null
          || targetIps == null
          || keyspaces == null
          || dataDir == null
          || createdAt == 0L
          || updatedAt == 0L) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new ClusterInfoRecord(this);
    }
  }
}
