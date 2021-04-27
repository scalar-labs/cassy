package com.scalar.cassy.service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.util.AzureUtil;
public abstract class AzureBlobContainerClientModule extends AbstractModule {
  private final String storeBaseUri;
  public AzureBlobContainerClientModule(String storeBaseUri) {
    this.storeBaseUri = storeBaseUri;
  }
  @Provides
  @Singleton
  public BlobContainerAsyncClient provideBlobContainerAsyncClient() {
    return AzureUtil.getBlobContainerAsyncClient(storeBaseUri);
  }

  @Provides
  @Singleton
  public BlobContainerClient provideBlobContainerClient() {
    return AzureUtil.getBlobContainerClient(storeBaseUri);
  }
}
