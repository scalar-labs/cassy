package com.scalar.cassy.service;

import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public class BackupService implements AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
  private final FileTraverser traverser;
  private final FileUploader uploader;

  @Inject
  public BackupService(FileTraverser traverser, FileUploader uploader) {
    this.traverser = traverser;
    this.uploader = uploader;
  }

  public void backup(BackupConfig config) {
    // Assumes that another incremental backups are not created after the last snapshot is taken.
    removeIncrementalBackups(config);

    List<Path> files = traverser.traverse(config.getKeyspace());
    uploader.upload(files, config);
  }

  @Override
  public void close() throws Exception {
    uploader.close();
  }

  private void removeIncrementalBackups(BackupConfig config) {
    if (config.getBackupType() == BackupType.NODE_INCREMENTAL) {
      return;
    }
    new IncrementalBackupTraverser(Paths.get(config.getDataDir()))
        .traverse(config.getKeyspace())
        .forEach(
            p -> {
              try {
                Files.delete(Paths.get(config.getDataDir(), p.toString()));
              } catch (IOException e) {
                logger.warn("removing incremental backup file " + p + " failed.", e);
              }
            });
  }
}
