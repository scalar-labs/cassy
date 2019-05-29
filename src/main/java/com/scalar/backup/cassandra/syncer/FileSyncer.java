package com.scalar.backup.cassandra.syncer;

import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.config.RestoreConfig;
import java.nio.file.Path;
import java.util.List;

public interface FileSyncer {

  void upload(List<Path> paths, BackupConfig config);

  void download(RestoreConfig config);
}
