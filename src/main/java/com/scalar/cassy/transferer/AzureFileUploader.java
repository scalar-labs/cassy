package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobAsyncClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureFileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AzureFileUploader.class);
  private final BlobAsyncClient blobClient;

  @Inject
  public AzureFileUploader(BlobAsyncClient blobClient) {
    this.blobClient = blobClient;
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    //    BlobUrlParts blobUri = BlobUrlParts.parse(config.getStoreBaseUri());
//    AtomicInteger count = new AtomicInteger();
//    files.forEach(
//        p -> {
//          String filePath = Paths.get(config.getDataDir(), p.toString()).toFile().getPath();
//          logger.info("Uploading file " + count + "/" + files.size() + " " + filePath);
//          count.getAndIncrement();
//          try {
//            blobClient
//                .uploadFromFile(filePath)
//                .doOnError(FileTransferException::new)
//                .subscribe(completion -> logger.info("Successfully uploaded file"));
//          } catch (RuntimeException e) {
//            throw new FileTransferException(e);
//          }
//        });

    // Test code that only uploads one file so that it doesn't take too long
    Path file = files.get(0);
    String filePath = Paths.get(config.getDataDir(), file.toString()).toFile().getPath();
    logger.info("Uploading the file");
    try {
      blobClient
          .uploadFromFile(filePath)
          .doOnError(FileTransferException::new)
          .subscribe(completion -> logger.info("Upload complete"));
    } catch (RuntimeException e) {
      throw new FileTransferException(e);
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
