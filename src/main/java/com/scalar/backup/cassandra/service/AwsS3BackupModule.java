package com.scalar.backup.cassandra.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.transferer.AwsS3FileUploader;
import com.scalar.backup.cassandra.transferer.FileUploader;
import com.scalar.backup.cassandra.traverser.FileTraverser;
import com.scalar.backup.cassandra.traverser.IncrementalBackupTraverser;
import com.scalar.backup.cassandra.traverser.SnapshotTraverser;
import java.nio.file.Paths;

public class AwsS3BackupModule extends AbstractModule {
  private final BackupType type;
  private final String dataDir;

  public AwsS3BackupModule(BackupType type, String dataDir) {
    this.type = type;
    this.dataDir = dataDir;
  }

  @Override
  protected void configure() {
    bind(FileUploader.class).to(AwsS3FileUploader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  FileTraverser provideFileTraverser() {
    if (type.equals(BackupType.NODE_INCREMENTAL)) {
      return new IncrementalBackupTraverser(Paths.get(dataDir));
    }
    return new SnapshotTraverser(Paths.get(dataDir));
  }

  @Provides
  @Singleton
  TransferManager provideTransferManager() {
    return TransferManagerBuilder.standard().build();
  }

  @Provides
  @Singleton
  AmazonS3 provideAmazonS3() {
    return AmazonS3ClientBuilder.defaultClient();
  }
}
