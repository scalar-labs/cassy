package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class AzureBlobFileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileDownloader.class);
  private BlobContainerAsyncClient blobContainerClient;

  @Inject
  public AzureBlobFileDownloader(BlobContainerAsyncClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public void download(RestoreConfig config) {
    String key = BackupPath.create(config, config.getKeyspace());

    logger.info("Downloading files of '" + config.getKeyspace() + "' keyspace");
    List<Mono<BlobProperties>> filesToBeDownloaded = new ArrayList<>();
    Iterable<BlobItem> keyspaceBlobs =
        blobContainerClient.listBlobs(new ListBlobsOptions().setPrefix(key + "/")).toIterable();
    for (BlobItem blob : keyspaceBlobs) {
      Path destFile = Paths.get(config.getDataDir(), blob.getName());
      try {
        Files.createDirectories(destFile.getParent());
      } catch (IOException e) {
        throw new FileTransferException(e);
      }
      filesToBeDownloaded.add(
          blobContainerClient
              .getBlobAsyncClient(blob.getName())
              .downloadToFile(destFile.toString())
              .doOnSuccess(
                  blobProperties -> logger.info("Download file succeeded : " + destFile.toString()))
              .doOnError(error -> logger.error("Download failed : " + destFile.toString(), error)));
    }

    // Start asynchronously all downloads and wait for all of them to complete
    try {
      Mono.when(filesToBeDownloaded).block();
    } catch (Exception e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() {}
}
