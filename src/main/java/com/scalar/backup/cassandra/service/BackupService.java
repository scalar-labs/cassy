package com.scalar.backup.cassandra.service;

import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.transferer.FileUploader;
import com.scalar.backup.cassandra.traverser.FileTraverser;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BackupService implements AutoCloseable {
  private final FileTraverser traverser;
  private final FileUploader uploader;

  @Inject
  public BackupService(FileTraverser traverser, FileUploader uploader) {
    this.traverser = traverser;
    this.uploader = uploader;
  }

  public void backup(BackupConfig config) {
    List<Path> files = traverser.traverse(config.getKeyspace());
    uploader.upload(files, config);
  }

  @Override
  public void close() throws Exception {
    uploader.close();
  }
}
