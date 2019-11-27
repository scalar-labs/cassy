package com.scalar.cassy.transferer;

import com.palantir.giraffe.file.MoreFiles;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileDownloader implements FileDownloader {

  private static final Logger logger = LoggerFactory.getLogger(FileSystemFileUploader.class);
  private final HostControlSystem remoteStorage;

  @Inject
  FileSystemFileDownloader(HostControlSystem remoteStorage) {
    this.remoteStorage = remoteStorage;
  }

  @Override
  public void download(RestoreConfig config) {
    try {
      String storagePath = URI.create(config.getStoreBaseUri()).getPath();
      Path backupKeyspace = remoteStorage
          .getPath(storagePath, BackupPath.create(config, config.getKeyspace()));
      Path targetKeyspace = Paths
          .get(config.getDataDir(), BackupPath.create(config, config.getKeyspace()));
      logger.info(String.format("Downloading keyspace \"%s\"", config.getKeyspace()));
      long start = System.currentTimeMillis();
      Files.createDirectories(targetKeyspace.getParent());
      MoreFiles.copyRecursive(backupKeyspace, targetKeyspace);
      long end = System.currentTimeMillis();
      logger.info(
          "Download completed in " + DurationFormatUtils
              .formatDurationWords(end - start, true, true));
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() {
    try {
      remoteStorage.close();
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }
}
