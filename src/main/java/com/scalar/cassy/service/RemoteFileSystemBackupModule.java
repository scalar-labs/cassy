package com.scalar.cassy.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.transferer.AwsS3FileUploader;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.transferer.RemoteFileSystemFileUploader;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.nio.file.Paths;

public class RemoteFileSystemBackupModule extends AbstractModule {
  private final BackupType type;
  private final String dataDir;
  private final String snapshotId;

  public RemoteFileSystemBackupModule(BackupType type, String dataDir, String snapshotId) {
    this.type = type;
    this.dataDir = dataDir;
    this.snapshotId = snapshotId;
  }

  @Override
  protected void configure() {
    bind(FileUploader.class).to(RemoteFileSystemFileUploader.class).in(Singleton.class);
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
