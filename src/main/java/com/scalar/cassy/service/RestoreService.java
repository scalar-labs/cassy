package com.scalar.cassy.service;

import com.google.inject.Inject;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.placer.Placer;
import com.scalar.cassy.transferer.FileDownloader;
import com.scalar.cassy.transferer.FileSystemFileDownloader;
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

    // This downloader preserve the file structure when creating the backup so placing the file is
    // not necessary after the download
    if (!(downloader instanceof FileSystemFileDownloader)) {
      return;
    }
    placer.place(config);
  }

  @Override
  public void close() throws Exception {
    downloader.close();
  }
}
