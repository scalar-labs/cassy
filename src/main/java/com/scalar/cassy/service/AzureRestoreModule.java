package com.scalar.cassy.service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AzureFileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AzureRestoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AzureFileDownloader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  BlobContainerAsyncClient provideBlobAsyncClient() {
    BlobServiceAsyncClient service =
        new BlobServiceClientBuilder()
            .connectionString(System.getenv("AZURE_STORAGE_CONNECTION_STRING"))
            .buildAsyncClient();
    return service.getBlobContainerAsyncClient("indetail-cassy-test");
  }
}

