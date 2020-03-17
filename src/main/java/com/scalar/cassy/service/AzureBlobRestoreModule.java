package com.scalar.cassy.service;

import static com.scalar.cassy.service.AzureBlobBackupModule.CONNECTION_STRING;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AzureBlobFileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AzureBlobRestoreModule extends AbstractModule {
  private final String storeBaseUri;

  public AzureBlobRestoreModule(String storeBaseUri) {
    this.storeBaseUri = storeBaseUri;
  }

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AzureBlobFileDownloader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  BlobContainerAsyncClient provideBlobAsyncClient() {
    if (System.getenv(CONNECTION_STRING) == null) {
      throw new IllegalArgumentException(
          "Please set the environment variable '" + CONNECTION_STRING + "'.");
    }
    return new BlobServiceClientBuilder()
        .connectionString(System.getenv(CONNECTION_STRING))
        .buildAsyncClient()
        .getBlobContainerAsyncClient(storeBaseUri);
  }
}
