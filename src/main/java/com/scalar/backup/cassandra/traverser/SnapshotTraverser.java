package com.scalar.backup.cassandra.traverser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class SnapshotTraverser extends FileTraverser {
  static final String SNAPSHOT_DIRNAME = "snapshots";
  private static final int DIR_TO_FILE_DISTANCE = 2;

  public SnapshotTraverser(Path dataDir) {
    super(dataDir);
  }

  @Override
  public List<Path> traverse(String keyspace) {
    return traverse(keyspace, null);
  }

  @Override
  public List<Path> traverse(String keyspace, @Nullable String table) {
    return traverse(keyspace, table, stream -> traverseSnapshot(stream));
  }

  private List<Path> traverseSnapshot(Stream<Path> stream) {
    return traverseFile(stream, SNAPSHOT_DIRNAME, DIR_TO_FILE_DISTANCE);
  }
}
