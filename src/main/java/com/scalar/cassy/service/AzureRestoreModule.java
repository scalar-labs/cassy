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
            .connectionString(
                "DefaultEndpointsProtocol=https;AccountName=cassydev;AccountKey=z7Eo2vQhE9y0VH0AE5O3GdgqrS6AnPdojRf08p+LSvbjMm1sr4hqJhXm8RPU4Cz1fxGUOlXAkzJxM61h5Rierw==;EndpointSuffix=core.windows.net")
            .buildAsyncClient();
    return service.getBlobContainerAsyncClient("indetail-cassy-test");
  }
}
