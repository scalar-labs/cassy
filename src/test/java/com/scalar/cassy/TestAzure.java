package com.scalar.cassy;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.scalar.cassy.exception.FileTransferException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Test;

public class TestAzure {

  private static final String CONNECTION_STRING =
      "DefaultEndpointsProtocol=https;AccountName=cassydev;AccountKey=yXdz8inmyiXO4vmgrrcaLbhRyRJ51VXROCt+cWfitjY1YlqEUkbPduf2o5uc+kknZh8ApRlT6NDyRVjfBbmeLw==;EndpointSuffix=core.windows.net";
  private static final String CONTAINER_NAME = "indetail-cassy-test";

  @Test
  public void main() {
    BlobContainerAsyncClient blobContainerClient = getAsyncClient();

    // upload a file
    CompletableFuture toBeUploaded =
        blobContainerClient
            .getBlobAsyncClient("testfile")
            .uploadFromFile("/Users/ben/Desktop/testfile", true)
            .toFuture();
    try {
      toBeUploaded.get();
    } catch (ExecutionException | InterruptedException e) {
      throw new FileTransferException(e);
    }
    // download the file with a different name
    CompletableFuture toBeDownloaded =
        blobContainerClient
            .getBlobAsyncClient("testfile")
            .downloadToFile("/Users/ben/Desktop/testfile2")
            .toFuture();
    try {
      toBeDownloaded.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new FileTransferException(e);
    }
  }

  private BlobContainerAsyncClient getAsyncClient() {
    return new BlobServiceClientBuilder()
        .connectionString(CONNECTION_STRING)
        .buildAsyncClient()
        .getBlobContainerAsyncClient(CONTAINER_NAME);
  }

  private BlobContainerClient getClient() {
    return new BlobServiceClientBuilder()
        .connectionString(CONNECTION_STRING)
        .buildClient()
        .getBlobContainerClient(CONTAINER_NAME);
  }
}
