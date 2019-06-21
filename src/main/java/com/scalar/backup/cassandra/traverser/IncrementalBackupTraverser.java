package com.scalar.backup.cassandra.traverser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class IncrementalBackupTraverser extends FileTraverser {
  static final String BACKUP_DIRNAME = "backups";
  public static final int DIR_TO_FILE_DISTANCE = 1;

  public IncrementalBackupTraverser(Path dataDir) {
    super(dataDir);
  }

  @Override
  public List<Path> traverse(String keyspace) {
    return traverse(keyspace, null);
  }

  @Override
  public List<Path> traverse(String keyspace, String table) {
    return traverse(keyspace, table, stream -> traverseBackup(stream));
  }

  private List<Path> traverseBackup(Stream<Path> stream) {
    return traverseFile(stream, BACKUP_DIRNAME, DIR_TO_FILE_DISTANCE);
  }
}
