package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.transferer.AzureBlobFileUploader;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.nio.file.Paths;

public class AzureBlobBackupModule extends AbstractModule {

  private final BackupType type;
  private final String dataDir;
  private final String snapshotId;

  public AzureBlobBackupModule(BackupType type, String dataDir, String snapshotId) {
    this.type = type;
    this.dataDir = dataDir;
    this.snapshotId = snapshotId;
  }

  @Override
  protected void configure() {
    bind(FileUploader.class).to(AzureBlobFileUploader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  FileTraverser provideFileTraverser() {
    if (type.equals(BackupType.NODE_INCREMENTAL)) {
      return new IncrementalBackupTraverser(Paths.get(dataDir));
    }
    return new SnapshotTraverser(Paths.get(dataDir), snapshotId);
  }
}
