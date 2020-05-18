package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.palantir.giraffe.file.MoreFiles;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteFileSystemFileDownloader extends RemoteFileSystemTransfererBase
    implements FileDownloader {
  private static final Logger logger = LoggerFactory.getLogger(RemoteFileSystemFileUploader.class);

  @Inject
  public RemoteFileSystemFileDownloader(RemoteFileSystemConnection hostConnection) {
    super(hostConnection);
  }

  @Override
  public void download(RestoreConfig config) {
    try {
      String remoteFileSystemStoragePath = URI.create(config.getStoreBaseUri()).getPath();
      Path backupKeyspacePath =
          hostConnection
              .getHostControlSystem()
              .getPath(
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
          String.format(
              "\"%s\" keyspace was downloaded in %s",
              config.getKeyspace(),
              DurationFormatUtils.formatDurationWords(end - start, true, true)));
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() throws IOException {
    this.hostConnection.getHostControlSystem().close();
  }

  /**
   * Wrapper used to copy a folder
   *
   * @param sourceDir
   * @param destDir
   * @throws IOException
   */
  @VisibleForTesting
  void copyFolder(Path sourceDir, Path destDir) throws IOException {
    MoreFiles.copyRecursive(sourceDir, destDir);
  }
}
