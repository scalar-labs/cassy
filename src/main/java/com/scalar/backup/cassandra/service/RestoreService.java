package com.scalar.backup.cassandra.service;

import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.placer.Placer;
import com.scalar.backup.cassandra.transferer.FileDownloader;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RestoreService implements AutoCloseable {
  private final FileDownloader downloader;
  private final Placer placer;

  @Inject
  public RestoreService(FileDownloader downloader, Placer placer) {
    this.downloader = downloader;
    this.placer = placer;
  }

  public void restore(RestoreConfig config) {
    downloader.download(config);
    placer.place(config);
  }

  @Override
  public void close() throws Exception {
    downloader.close();
  }
}
