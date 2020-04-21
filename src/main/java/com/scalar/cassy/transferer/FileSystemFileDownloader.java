package com.scalar.cassy.transferer;

import com.google.inject.Inject;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileDownloader extends FileSystemTransfererBase implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(FileSystemFileUploader.class);
  private final HostControlSystem hostConnection;

  @Inject
  public FileSystemFileDownloader(HostControlSystem hostConnection) {
    this.hostConnection = hostConnection;
  }

  @Override
  public void download(RestoreConfig config) {
    try {
      String remoteFileSystemStoragePath = URI.create(config.getStoreBaseUri()).getPath();
      Path backupKeyspacePath =
          hostConnection.getPath(
              remoteFileSystemStoragePath, BackupPath.create(config, config.getKeyspace()));
      Path destDir =
          Paths.get(config.getDataDir(), BackupPath.create(config, config.getKeyspace()));
      logger.info(String.format("Downloading keyspace \"%s\"", config.getKeyspace()));
      long start = System.currentTimeMillis();
      createDirectories(destDir.getParent());
      // Copy keyspace files
      copyFolder(backupKeyspacePath, destDir);
      long end = System.currentTimeMillis();
      logger.info(
          "Download completed in "
              + DurationFormatUtils.formatDurationWords(end - start, true, true));
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() throws IOException {
    this.hostConnection.close();
  }
}
