package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileIOException;
import com.scalar.cassy.exception.FileTransferException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureBlobFileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileUploader.class);
  private static final int NUM_THREADS = 3;
  private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
  private final BlobContainerClient blobContainerClient;

  @Inject
  public AzureBlobFileUploader(BlobContainerClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public Future<Void> upload(Path file, String key) {
    if (!requiresUpload(key, file)) {
      logger.info(file + " has been already uploaded.");
      return CompletableFuture.completedFuture(null);
    }

    logger.info("Uploading " + file);
    return executorService.submit(
            () -> {
              try {
                try (InputStream inputStream = new FileInputStream(file.toString())) {
                  blobContainerClient
                          .getBlobClient(key)
                          .upload(inputStream, new File(file.toString()).length());
                }
              } catch (IOException e) {
                throw new FileTransferException(e);
              }
              return null;
            });
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    Path dataDir = Paths.get(config.getDataDir());
    List<Future> uploads = new ArrayList<>();
    for (Path filePath : files) {
      Path relative = dataDir.relativize(filePath);
      String key = BackupPath.create(config, relative.toString());
      if (requiresUpload(key, filePath)) {
        logger.info("Uploading " + filePath);
        uploads.add(
            executorService
              .submit(() -> {
                try {
                  try (InputStream inputStream = new FileInputStream(filePath.toString())) {
                    blobContainerClient
                      .getBlobClient(key)
                      .upload(inputStream, new File(filePath.toString()).length());
                  }
                } catch (IOException e) {
                  throw new FileTransferException(e);
                }
                logger.info("Upload file succeeded : " + filePath.toString());
             }));
      } else {
        logger.info(filePath + " has been already uploaded.");
      }
    }

    uploads.forEach(u -> {
      try{
        // Start upload files asynchronously and wait for them to complete
        u.get();
      } catch (InterruptedException | ExecutionException e){
        throw new FileTransferException(e);
      }
    });
  }

  @Override
  public void close() {}

  @VisibleForTesting
  boolean requiresUpload(String key, Path file) {
    try {
      BlobClient client = blobContainerClient.getBlobClient(key);
      BlobProperties blobProperties = client.getProperties();

      if (blobProperties.getBlobSize() == Files.size(file)) {
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
