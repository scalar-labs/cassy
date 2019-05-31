package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.BackupConfig;

public interface FileUploader {

  void upload(BackupConfig config);
}
