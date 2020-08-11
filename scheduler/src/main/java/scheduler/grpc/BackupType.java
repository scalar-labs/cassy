package scheduler.grpc;

public enum BackupType {
  CLUSTER_SNAPSHOT(1),
  NODE_SNAPSHOT(2),
  NODE_INCREMENTAL(3);

  private final int type;

  BackupType(int type) {
    this.type = type;
  }

  public int get() {
    return this.type;
  }

  public static BackupType getByType(int type) {
    for (BackupType t : BackupType.values()) {
      if (t.type == type) {
        return t;
      }
    }
    return null;
  }
}
