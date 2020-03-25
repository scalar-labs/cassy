package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.FileDownloader;
import com.scalar.cassy.transferer.FileSystemFileDownloader;
import java.net.URI;

public class FileSystemRestoreModule extends AbstractModule {
  private final URI storageURI;

  public FileSystemRestoreModule(URI storageURI) {
    this.storageURI = storageURI;
  }

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(FileSystemFileDownloader.class).in(Singleton.class);
  }
}
