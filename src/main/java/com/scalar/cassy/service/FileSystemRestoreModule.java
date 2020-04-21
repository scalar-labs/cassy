package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.transferer.FileDownloader;
import com.scalar.cassy.transferer.FileSystemFileDownloader;
import com.scalar.cassy.util.GiraffeUtil;
import java.io.IOException;
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

  @Provides
  HostControlSystem provideHostControlSystem() throws IOException {
    return GiraffeUtil.openSshConnection(storageURI);
  }
}
