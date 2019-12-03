package com.scalar.cassy.transferer;

import com.azure.core.http.rest.PagedFlux;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureFileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AzureFileDownloader.class);
  private final BlobContainerAsyncClient blobContainerClient;

  @Inject
  public AzureFileDownloader(BlobContainerAsyncClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public void download(RestoreConfig config) {
    String key = BackupPath.create(config, config.getKeyspace());
    ListBlobsOptions options = new ListBlobsOptions().setPrefix(key);
    PagedFlux<BlobItem> blobItemPagedFlux = blobContainerClient.listBlobs(options);
    List<CompletableFuture> futures = new ArrayList<>();
    blobItemPagedFlux
        .toIterable()
        .forEach(
            b -> {
              try {
                Files.createDirectories(Paths.get(config.getDataDir(), b.getName()).getParent());
              } catch (IOException e) {
                throw new FileTransferException(e);
              }
              futures.add(
                  blobContainerClient
                      .getBlobAsyncClient(b.getName())
                      .downloadToFile(Paths.get(config.getDataDir(), b.getName()).toString())
                      .toFuture());
            });

    logger.info("Downloading " + blobContainerClient.getBlobContainerName() + key);
    futures.forEach(
        f -> {
          try {
            f.get();
          } catch (InterruptedException | ExecutionException e) {
            throw new FileTransferException(e);
          }
        });
  }

  @Override
  public void close() {
    System.out.println("CLOSING THE AZURE DOWNLOADER");
  }
}
