package com.scalar.cassy.util;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class AzureUtil {
  private static final String CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";

  public static BlobContainerAsyncClient getBlobContainerAsyncClient(String storageBaseUri) {
    String connectionString = System.getenv(CONNECTION_STRING);
    if (connectionString == null) {
      throw new IllegalArgumentException(
          "Please set the environment variable '" + CONNECTION_STRING + "'.");
    }
    BlobServiceAsyncClient asyncClient =
        new BlobServiceClientBuilder().connectionString(connectionString).buildAsyncClient();
    if (!storageBaseUri.startsWith(asyncClient.getAccountUrl())) {
      throw new IllegalArgumentException(
          "The container does not belong to the same blob of the "
              + CONNECTION_STRING
              + " environment variable");
    }
    String containerName = storageBaseUri.replace(asyncClient.getAccountUrl() + "/", "");
    return asyncClient.getBlobContainerAsyncClient(containerName);
  }
}
