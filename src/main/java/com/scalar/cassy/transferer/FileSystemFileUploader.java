package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileUploader extends FileSystemTransfererBase implements FileUploader {

  private static final Logger logger = LoggerFactory.getLogger(FileSystemFileUploader.class);
  private final HostControlSystem hostConnection;

  @Inject
  public FileSystemFileUploader(HostControlSystem hostConnection) {
    this.hostConnection = hostConnection;
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    try {
      String remoteFileSystemStoragePath = URI.create(config.getStoreBaseUri()).getPath();
      Path destinationDir =
          hostConnection
              .getPath(remoteFileSystemStoragePath, BackupPath.create(config, config.getKeyspace()))
              .getParent();
      Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
      logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
      long uploadStart = System.currentTimeMillis();
      // Create keyspace folder structure on remote file system
      createDirectories(destinationDir);
      //       Copy keyspace files
      executeCommand(
          Commands.get(
              "rsync",
              "-rzPe",
              "ssh",
              sourceDir.toAbsolutePath(),
              String.format("%s:%s", getHost(config), destinationDir.toAbsolutePath())));
      long uploadEnd = System.currentTimeMillis();
      logger.info(
          "Upload completed in "
              + DurationFormatUtils.formatDurationWords(uploadEnd - uploadStart, true, true));
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() throws Exception {
    this.hostConnection.close();
  }

  /**
   * Wrapper used to create directories. It was created to facilitate testing.
   *
   * @param dir
   * @throws IOException
   */
  @VisibleForTesting
  void createDirectories(Path dir) throws IOException {
    Files.createDirectories(dir);
  }
}
