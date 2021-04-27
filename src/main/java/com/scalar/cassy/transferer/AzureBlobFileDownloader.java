package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureBlobFileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AzureBlobFileDownloader.class);
  private BlobContainerClient blobContainerClient;
  private static final int NUM_THREADS = 3;
  private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

  @Inject
  public AzureBlobFileDownloader(BlobContainerClient blobContainerClient) {
    this.blobContainerClient = blobContainerClient;
  }

  @Override
  public void download(RestoreConfig config) {
    List<Future> downloadFuture = new ArrayList<>();
    String key = BackupPath.create(config, config.getKeyspace());

    logger.info("Downloading " + blobContainerClient.getBlobContainerName() + "/" + key);
    Iterator<BlobItem> keyspaceBlobs =
            blobContainerClient.listBlobs(new ListBlobsOptions().setPrefix(key + "/"), null).iterator();
    while (keyspaceBlobs.hasNext()) {
      BlobItem blob = keyspaceBlobs.next();
      Path destFile = Paths.get(config.getDataDir(), blob.getName());

      try {
        Files.createDirectories(destFile.getParent());
      } catch (IOException e) {
        throw new FileTransferException(e);
      }

      downloadFuture.add(
        executorService
          .submit(() -> {
            try {
              try (OutputStream outputStream = new FileOutputStream(destFile.toString())) {
                blobContainerClient
                  .getBlobClient(blob.getName())
                  .download(outputStream);
              }
            } catch (IOException e) {
              throw new FileTransferException(e);
            }
            logger.info("Download file succeeded : " + destFile.toString());
          }));
    }

    downloadFuture.forEach(d->{
      try{
        // Start downloading files asynchronously and wait for them to complete
        d.get();
      }catch (InterruptedException | ExecutionException e){
        throw new FileTransferException(e);
      }
    });
  }

  @Override
  public void close() {}
}
