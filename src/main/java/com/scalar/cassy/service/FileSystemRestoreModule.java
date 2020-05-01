package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.FileDownloader;
import com.scalar.cassy.transferer.RemoteFileSystemFileDownloader;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.IOException;
import java.net.URI;

public class FileSystemRestoreModule extends AbstractModule {
  private final URI storageURI;

  public FileSystemRestoreModule(URI storageURI) {
    this.storageURI = storageURI;
  }

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(RemoteFileSystemFileDownloader.class).in(Singleton.class);
  }

  @Provides
  RemoteFileSystemConnection provideHostControlSystem() throws IOException {
    return new RemoteFileSystemConnection(storageURI);
  }
}
