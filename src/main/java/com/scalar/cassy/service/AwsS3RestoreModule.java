package com.scalar.cassy.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AwsS3FileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AwsS3RestoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AwsS3FileDownloader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  TransferManager provideTransferManager() {
    return TransferManagerBuilder.standard().build();
  }

  @Provides
  @Singleton
  AmazonS3 provideAmazonS3() {
    return AmazonS3ClientBuilder.defaultClient();
  }
}

