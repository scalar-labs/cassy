package com.scalar.cassy.service;

import com.google.inject.Singleton;
import com.scalar.cassy.transferer.AzureBlobFileDownloader;
import com.scalar.cassy.transferer.FileDownloader;

public class AzureBlobRestoreModule extends AzureBlobContainerClientModule {

  public AzureBlobRestoreModule(String storeBaseUri) {
    super(storeBaseUri);
  }

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(AzureBlobFileDownloader.class).in(Singleton.class);
  }
}
