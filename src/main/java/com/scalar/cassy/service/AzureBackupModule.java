package com.scalar.cassy.service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.transferer.AzureFileUploader;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.nio.file.Paths;

public class AzureBackupModule extends AbstractModule {
  private final BackupType type;
  private final String dataDir;
  private final String snapshotId;
  private final String storeBaseUri;
  public static final String AZURE_STORAGE_CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";

  public AzureBackupModule(BackupType type, String dataDir, String snapshotId, String storeBaseUri) {
    this.type = type;
    this.dataDir = dataDir;
    this.snapshotId = snapshotId;
    this.storeBaseUri = storeBaseUri;
  }

  @Override
  protected void configure() {
    bind(FileUploader.class).to(AzureFileUploader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  FileTraverser provideFileTraverser() {
    if (type.equals(BackupType.NODE_INCREMENTAL)) {
      return new IncrementalBackupTraverser(Paths.get(dataDir));
    }
    return new SnapshotTraverser(Paths.get(dataDir), snapshotId);
  }

  @Provides
  @Singleton
  BlobContainerAsyncClient provideBlobAsyncClient() {
    if (System.getenv(AZURE_STORAGE_CONNECTION_STRING) == null) {
      throw new IllegalArgumentException(
          "Please set the environment variable '" + AZURE_STORAGE_CONNECTION_STRING + "'.");
    }
    return new BlobServiceClientBuilder()
        .connectionString(System.getenv(AZURE_STORAGE_CONNECTION_STRING))
        .buildAsyncClient()
        .getBlobContainerAsyncClient(storeBaseUri);
  }
}
