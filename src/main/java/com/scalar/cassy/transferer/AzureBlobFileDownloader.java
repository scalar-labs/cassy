package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureBlobFileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileDownloader.class);
  private static final int NUM_THREADS = 3;
  private static final int ASYNC_FILE_DOWNLOAD_LIMIT = 20;
  private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
  private final BlobContainerClient blobContainerClient;

  @Inject
  public AzureBlobFileDownloader(BlobContainerClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public void download(RestoreConfig config) {
    List<Future<Void>> downloadFuture = new ArrayList<>();
    String key = BackupPath.create(config, config.getKeyspace());

    logger.info("Downloading " + blobContainerClient.getBlobContainerName() + "/" + key);
    for (BlobItem blob : listBlobs(key)) {
      Path destFile = Paths.get(config.getDataDir(), blob.getName());
      try {
        Files.createDirectories(destFile.getParent());
      } catch (IOException e) {
        throw new FileTransferException(e);
      }
      downloadFuture.add(
          executorService.submit(
              () -> {
                try (OutputStream outputStream = writeStream(destFile)) {
                  blobContainerClient.getBlobClient(blob.getName()).download(outputStream);
                } catch (IOException e) {
                  throw new FileTransferException(e);
                }
                logger.info("Download file succeeded : " + destFile);
                return null;
              }));

      if (downloadFuture.size() >= ASYNC_FILE_DOWNLOAD_LIMIT) {
        waitForAsyncFileDownload(downloadFuture);
        downloadFuture.clear();
      }
    }

    if (downloadFuture.size() > 0) {
      waitForAsyncFileDownload(downloadFuture);
    }
  }

  private void waitForAsyncFileDownload(List<Future<Void>> downloadFuture) {
    downloadFuture.forEach(
        d -> {
          try {
            // Start download files asynchronously and wait for them to complete
            d.get();
          } catch (InterruptedException | ExecutionException e) {
            throw new FileTransferException(e);
          }
        });
  }

  @Override
  public void close() {}

  @VisibleForTesting
  OutputStream writeStream(Path path) {
    try {
      return new FileOutputStream(path.toString());
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @VisibleForTesting
  Iterable<BlobItem> listBlobs(String key) {
    return blobContainerClient.listBlobs(new ListBlobsOptions().setPrefix(key + "/"), null);
  }
}
