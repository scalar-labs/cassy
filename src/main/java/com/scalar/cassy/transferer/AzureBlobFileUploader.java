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
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class AzureBlobFileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileUploader.class);
  private final BlobContainerAsyncClient blobContainerClient;

  @Inject
  public AzureBlobFileUploader(BlobContainerAsyncClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public Future<Void> upload(Path file, String key, String storageBaseUri) {
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
    List<Mono<Void>> toBeUploaded = new ArrayList<>();
    for (Path filePath : files) {
      Path relative = dataDir.relativize(filePath);
      String key = BackupPath.create(config, relative.toString());
      if (requiresUpload(key, filePath)) {
        logger.info("Uploading " + filePath);
        toBeUploaded.add(
            blobContainerClient
                .getBlobAsyncClient(key)
                .uploadFromFile(filePath.toString(), true)
                .doOnSuccess(blobProperties -> logger.info("Upload succeeded : " + filePath))
                .doOnError(
                    error -> {
                      throw new FileTransferException("Upload failed : " + filePath, error);
                    }));
      } else {
        logger.info(filePath + " has been already uploaded.");
      }
    }

    // Start asynchronously all uploads and wait for all of them to complete
    Mono.when(toBeUploaded).block();
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
