package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobAsyncClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    files.forEach(
        p -> {
          String filePath = Paths.get(config.getDataDir(), p.toString()).toFile().getPath();
          logger.info("Uploading " + filePath);
          try {
            blobClient
                .uploadFromFile(filePath)
                .doOnError(FileTransferException::new)
                .subscribe(completion -> logger.info("Successfully uploaded file"));
          } catch (RuntimeException e) {
            throw new FileTransferException(e);
          }
        });

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
