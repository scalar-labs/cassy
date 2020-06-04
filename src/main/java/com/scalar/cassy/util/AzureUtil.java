package com.scalar.cassy.util;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class AzureUtil {
  private static final String CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";

  public static BlobContainerAsyncClient getBlobContainerAsyncClient(String storageBaseUri) {
    BlobServiceClientBuilder builder = new BlobServiceClientBuilder();

    String connectionString = System.getenv(CONNECTION_STRING);
    if (connectionString == null) {
      // Use service principal or managed identity when a connection string is not given
      String accountUrl = storageBaseUri.substring(0, storageBaseUri.lastIndexOf('/'));
      builder.endpoint(accountUrl).credential(new DefaultAzureCredentialBuilder().build());
    } else {
      // Use a specified connection string when given
      builder.connectionString(connectionString);
    }
    BlobServiceAsyncClient asyncClient = builder.buildAsyncClient();

    if (!storageBaseUri.startsWith(asyncClient.getAccountUrl())) {
      throw new IllegalArgumentException(
          "The given credential can not be used for the specified container.");
    }
    String containerName = storageBaseUri.replace(asyncClient.getAccountUrl() + "/", "");
    return asyncClient.getBlobContainerAsyncClient(containerName);
  }
}
