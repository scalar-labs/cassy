package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.BackupConfig;

public interface FileUploader extends AutoCloseable {

  void upload(BackupConfig config);
}
