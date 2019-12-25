package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileIOException;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureFileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AzureFileUploader.class);
  private final BlobContainerAsyncClient blobContainerClient;

  @Inject
  public AzureFileUploader(BlobContainerAsyncClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    AtomicInteger count = new AtomicInteger();
    count.getAndIncrement();
    List<CompletableFuture> toBeUploaded = new ArrayList<>();
    files.forEach(
        p -> {
          String key = BackupPath.create(config, p.toString());
          Path filePath = Paths.get(config.getDataDir(), p.toString());
          if (requiresUpload(key, filePath)) {
            logger.info("Uploading file " + count + "/" + files.size() + " " + filePath);
            count.getAndIncrement();
            toBeUploaded.add(
                blobContainerClient
                    .getBlobAsyncClient(key)
                    .uploadFromFile(filePath.toString(), true)
                    .toFuture());
          }
        });
    for (CompletableFuture future : toBeUploaded) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new FileTransferException(e);
      }
    }
  }

  @Override
  public void close() {}

  @VisibleForTesting
  boolean requiresUpload(String key, Path file) {
    try {
      BlobAsyncClient client = blobContainerClient.getBlobAsyncClient(key);
      Optional<BlobProperties> blobProperties = client.getProperties().blockOptional();

      if (blobProperties.isPresent() && blobProperties.get().getBlobSize() == Files.size(file)) {
        return false;
      }
    } catch (IOException e) {
      throw new FileIOException(e);
    } catch (BlobStorageException e) {
      if (e.getStatusCode() == 404) {
        return true;
      }
      throw new FileTransferException(e);
    }
    return true;
  }
}

