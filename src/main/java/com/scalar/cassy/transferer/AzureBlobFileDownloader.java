package com.scalar.cassy.transferer;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    logger.info("Downloading " + blobContainerClient.getBlobContainerName() + "/" + key);
    Iterable<BlobItem> keyspaceBlobs =
            blobContainerClient.listBlobs(new ListBlobsOptions().setPrefix(key + "/"), null).toIterable();
    CountDownLatch countDownLatch = new CountDownLatch(Iterators.size(keyspaceBlobs.iterator()));
    Iterator<BlobItem> blobList = keyspaceBlobs.iterator();

    while (blobList.hasNext()) {
      BlobItem blob = blobList.next();
      Path destFile = Paths.get(config.getDataDir(), blob.getName());

      try {
        Files.createDirectories(destFile.getParent());
      } catch (IOException e) {
        throw new FileTransferException(e);
      }

      try {
        OutputStream outputStream = new FileOutputStream(destFile.toString());
          blobContainerClient
                  .getBlobAsyncClient(blob.getName())
                  .download()
                  .subscribe(piece->{
                    try {
                      outputStream.write(piece.array());
                    }catch (IOException e){
                      countDownLatch.countDown();
                      throw new FileTransferException(e);
                    }
                  }, ex ->{
                    countDownLatch.countDown();
                    throw new FileTransferException(ex);
                  }, ()->{
                    countDownLatch.countDown();
                    logger.info("Download file succeeded : " + destFile.toString());
                  });
      } catch (IOException e) {
        countDownLatch.countDown();
        throw new FileTransferException(e);
      }
    }

    try {
      //waiting for asynchronous download completion
      countDownLatch.await();
    }catch (InterruptedException ex){
      throw new FileTransferException(ex);
    }
  }

  @Override
  public void close() {}
}
