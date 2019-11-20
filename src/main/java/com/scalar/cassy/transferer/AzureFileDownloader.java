package com.scalar.cassy.transferer;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureFileDownloader implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(AwsS3FileDownloader.class);
  private final TransferManager manager;

  @Inject
  public AzureFileDownloader(TransferManager manager) {
    this.manager = manager;
  }

  @Override
  public void download(RestoreConfig config) {

  }

  @Override
  public void close() { manager.shutdownNow(); }
}
