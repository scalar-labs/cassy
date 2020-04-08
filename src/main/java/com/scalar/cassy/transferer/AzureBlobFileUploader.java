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
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureBlobFileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileUploader.class);
  private final BlobContainerAsyncClient blobContainerClient;

  @Inject
  public AzureBlobFileUploader(BlobContainerAsyncClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public Future<Void> upload(Path file, String key) {
    if (!requiresUpload(key, file)) {
      logger.info(file + " has been already uploaded.");
      return CompletableFuture.completedFuture(null);
    }

    logger.info("Uploading " + file);
    return blobContainerClient
        .getBlobAsyncClient(key)
        .uploadFromFile(file.toString(), true)
        .toFuture();
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    Path dataDir = Paths.get(config.getDataDir());
    List<CompletableFuture> toBeUploaded = new ArrayList<>();

    files.forEach(
        p -> {
          Path relative = dataDir.relativize(p);
          String key = BackupPath.create(config, relative.toString());
          if (requiresUpload(key, p)) {
            logger.info("Uploading " + p);
            toBeUploaded.add(
                blobContainerClient
                    .getBlobAsyncClient(key)
                    .uploadFromFile(p.toString(), true)
                    .toFuture());
          } else {
            logger.info(p + " has been already uploaded.");
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
