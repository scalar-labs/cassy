package com.scalar.cassy.service;

import com.azure.identity.CredentialBuilderBase;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
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

  public AzureBackupModule(BackupType type, String dataDir, String snapshotId) {
    this.type = type;
    this.dataDir = dataDir;
    this.snapshotId = snapshotId;
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
    BlobServiceAsyncClient service = new BlobServiceClientBuilder()
        .connectionString("DefaultEndpointsProtocol=https;AccountName=cassydev;AccountKey=yXdz8inmyiXO4vmgrrcaLbhRyRJ51VXROCt+cWfitjY1YlqEUkbPduf2o5uc+kknZh8ApRlT6NDyRVjfBbmeLw==;EndpointSuffix=core.windows.net")
        .buildAsyncClient();
    return service.getBlobContainerAsyncClient("indetail-cassy-test");

  }

//  @Provides
//  @Singleton
//  BlobAsyncClient provideBlobClient() {
//    return new BlobClientBuilder()
//        .connectionString("DefaultEndpointsProtocol=https;AccountName=cassydev;AccountKey=z7Eo2vQhE9y0VH0AE5O3GdgqrS6AnPdojRf08p+LSvbjMm1sr4hqJhXm8RPU4Cz1fxGUOlXAkzJxM61h5Rierw==;EndpointSuffix=core.windows.net")
//        .blobName("myblob")
//        .buildAsyncClient();
//  }
}
