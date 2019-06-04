package com.scalar.backup.cassandra.transferer;

import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.exception.FileTransferException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsS3FileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AwsS3FileDownloader.class);
  private final TransferManager manager;

  @Inject
  public AwsS3FileDownloader(TransferManager manager) {
    this.manager = manager;
  }

  @Override
  public void download(RestoreConfig config) {
    String key = BackupPath.create(config, config.getKeyspace());
    AmazonS3URI s3Uri = new AmazonS3URI(config.getStoreBaseUri());

    try {
      logger.info("Downloading " + s3Uri.getBucket() + "/" + key);
      MultipleFileDownload download =
          manager.downloadDirectory(
              s3Uri.getBucket(), key, Paths.get(config.getDataDir()).toFile());
      download.waitForCompletion();
      logger.info(download.getDescription() + " - " + download.getState().name());
    } catch (InterruptedException | RuntimeException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() {
    manager.shutdownNow();
  }
}
