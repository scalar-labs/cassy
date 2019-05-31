package com.scalar.backup.cassandra.service;

import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.transferer.FileUploader;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupService implements AutoCloseable {
  private final FileUploader uploader;

  @Inject
  public BackupService(FileUploader uploader) {
    this.uploader = uploader;
  }

  public void backup(BackupConfig config) {
    uploader.upload(config);
  }

  @Override
  public void close() throws Exception {
    uploader.close();
  }
}
