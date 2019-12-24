package com.scalar.cassy.service;

import static com.scalar.cassy.service.AzureBackupModule.AZURE_STORAGE_CONNECTION_STRING;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AzureFileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AzureRestoreModule extends AbstractModule {
  private final String storeBaseUri;

  public AzureRestoreModule(String storeBaseUri) {
    this.storeBaseUri = storeBaseUri;
  }

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AzureFileDownloader.class).in(Singleton.class);
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
