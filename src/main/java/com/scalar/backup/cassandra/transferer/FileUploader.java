package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.BackupConfig;
import java.nio.file.Path;
import java.util.List;

public interface FileUploader extends AutoCloseable {

  void upload(List<Path> files, BackupConfig config);
}
