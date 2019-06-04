package com.scalar.backup.cassandra.service;

import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.transferer.FileDownloader;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RestoreService implements AutoCloseable {
  private final FileDownloader downloader;

  @Inject
  public RestoreService(FileDownloader downloader) {
    this.downloader = downloader;
  }

  public void restore(RestoreConfig config) {
    downloader.download(config);
  }

  @Override
  public void close() throws Exception {
    downloader.close();
  }
}
