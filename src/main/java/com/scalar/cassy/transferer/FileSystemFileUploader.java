package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
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
  public Future<Void> upload(Path file, String key, String storageBaseUri) {
    String remoteFileSystemStoragePath = URI.create(storageBaseUri).getPath();
    Path targetFilePath = hostConnection.getPath(remoteFileSystemStoragePath, key);
    File targetFile = new File(targetFilePath.toString());
    if (targetFile.exists() && targetFile.length() == file.toFile().length()) {
      logger.info(file + " has been already uploaded.");
      return CompletableFuture.completedFuture(null);
    }

    logger.info("Uploading " + file);
    try {
      createDirectories(targetFilePath.getParent());
      copyFile(file, targetFilePath);
      return CompletableFuture.completedFuture(null);
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    try {
      String remoteFileSystemStoragePath = URI.create(config.getStoreBaseUri()).getPath();
      Path destinationDir =
          hostConnection.getPath(
              remoteFileSystemStoragePath, BackupPath.create(config, config.getKeyspace()));
      Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
      logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
      long uploadStart = System.currentTimeMillis();
      // Create snapshot folder structure on the remote file system
      createDirectories(destinationDir.getParent());
      // Copy keyspace files
      copyFolder(sourceDir, destinationDir);
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

  @VisibleForTesting
  void copyFile(Path source, Path target) throws IOException {
    Files.copy(source,target, StandardCopyOption.REPLACE_EXISTING);
  }
}
