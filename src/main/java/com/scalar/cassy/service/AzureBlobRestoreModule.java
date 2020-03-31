package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AzureBlobFileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AzureBlobRestoreModule extends AbstractModule {

  public AzureBlobRestoreModule() {}

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AzureBlobFileDownloader.class).in(Singleton.class);
  }
}
