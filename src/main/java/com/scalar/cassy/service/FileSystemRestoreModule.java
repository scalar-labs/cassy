package com.scalar.cassy.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.scalar.cassy.transferer.FileDownloader;
import com.scalar.cassy.transferer.FileSystemFileDownloader;

public class FileSystemRestoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(FileDownloader.class).to(FileSystemFileDownloader.class).in(Singleton.class);
  }
}
