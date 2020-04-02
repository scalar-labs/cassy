package com.scalar.cassy.service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public abstract class AzureBlobContainerClientModule extends AbstractModule {
  public static final String CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";
  private final String storeBaseUri;

  public AzureBlobContainerClientModule(String storeBaseUri) {
    this.storeBaseUri = storeBaseUri;
  }

  @Provides
  @Singleton
  public BlobContainerAsyncClient provideBlobContainerAsyncClient() {
    String connectionString = System.getenv(CONNECTION_STRING);
    if (connectionString == null) {
      throw new IllegalArgumentException(
          "Please set the environment variable '" + CONNECTION_STRING + "'.");
    }
    BlobServiceAsyncClient asyncClient =
        new BlobServiceClientBuilder().connectionString(connectionString).buildAsyncClient();
    if (!storeBaseUri.startsWith(asyncClient.getAccountUrl())) {
      throw new IllegalArgumentException(
          "The container does not belong to the same blob of the "
              + CONNECTION_STRING
              + " environment variable");
    }
    String containerName = storeBaseUri.replace(asyncClient.getAccountUrl() + "/", "");
    return asyncClient.getBlobContainerAsyncClient(containerName);
  }
}
