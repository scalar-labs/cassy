package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
          String filePath = Paths.get(config.getDataDir(), p.toString()).toFile().getPath();
          logger.info("Uploading file " + count + "/" + files.size() + " " + filePath);
          count.getAndIncrement();
          toBeUploaded.add(
              blobContainerClient
                  .getBlobAsyncClient(key)
                  .uploadFromFile(filePath, true)
                  .doOnError(FileTransferException::new)
                  .toFuture());
        });
    for (CompletableFuture future : toBeUploaded) {
      try {
        future.get();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void close() throws Exception {}

  @VisibleForTesting
  boolean requiresUpload(Path file) {
    //    try {
    //      long blobSize = blobClient.getBlockBlobAsyncClient().getProperties().subscribe()
    //
    //    } catch (IOException e) {
    //
    //    }
    //
    //
    //
    //
    //
    //    BlobServiceClient client = new BlobServiceClientBuilder().buildClient();
    //    BlobContainerClient c = client.getBlobContainerClient("");
    //    c.getProperties();
    //    c.listBlobs();
    return true;
  }
}
