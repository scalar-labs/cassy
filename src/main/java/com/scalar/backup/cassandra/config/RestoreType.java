package com.scalar.backup.cassandra.config;

public enum RestoreType {
  CLUSTER(1),
  NODE(2);

  private final int type;

  RestoreType(int type) {
    this.type = type;
  }

  public int get() {
    return this.type;
  }

  public static RestoreType getByType(int type) {
    for (RestoreType t : RestoreType.values()) {
      if (t.type == type) {
        return t;
      }
    }
    return null;
  }
}
