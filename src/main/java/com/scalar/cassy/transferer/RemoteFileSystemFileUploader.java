package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.palantir.giraffe.file.MoreFiles;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteFileSystemFileUploader extends RemoteFileSystemTransfererBase
    implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(RemoteFileSystemFileUploader.class);
  private static final int NB_PARALLEL_UPLOAD_THREADS = 1;

  @Inject
  public RemoteFileSystemFileUploader(RemoteFileSystemConnection hostConnection) {
    super(hostConnection);
  }

  @Override
  public Future<Void> upload(Path file, String key) {
    String remoteFileSystemStoragePath = URI.create(hostConnection.getStoragePath()).getPath();
    Path targetFilePath =
        hostConnection.getHostControlSystem().getPath(remoteFileSystemStoragePath, key);
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
    String remoteFileSystemStoragePath = URI.create(config.getStoreBaseUri()).getPath();
    Path dataDir = Paths.get(config.getDataDir());
    ExecutorService executorService = Executors.newFixedThreadPool(NB_PARALLEL_UPLOAD_THREADS);
    ExecutorCompletionService<Void> executorCompletionService =
        new ExecutorCompletionService<>(executorService);
    logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
    long uploadStart = System.currentTimeMillis();

    // Start uploading files
    for (Path filePath : files) {
      Path targetFilePath =
          computeTargetFilePath(config, remoteFileSystemStoragePath, dataDir, filePath);
      try {
        this.createDirectories(targetFilePath.getParent());
      } catch (IOException e) {
        throw new FileTransferException(e);
      }
      submitFileUploadTask(executorCompletionService, filePath, targetFilePath);
    }

    // Wait for uploads completion
    int completedUploads = 0;
    while (completedUploads < files.size()) {
      try {
        executorCompletionService.take().get();
        completedUploads++;
      } catch (Exception e) {
        executorService.shutdownNow();
        throw new FileTransferException(e);
      }
    }
    long uploadEnd = System.currentTimeMillis();
    logger.info(
        String.format(
            "\"%s\" keyspace was uploaded in %s",
            config.getKeyspace(),
            DurationFormatUtils.formatDurationWords(uploadEnd - uploadStart, true, true)));
    executorService.shutdownNow();
  }

  private void submitFileUploadTask(
      ExecutorCompletionService<Void> executorService, Path sourceFile, Path targetFile) {
    executorService.submit(
        () -> {
          logger.info("Uploading " + sourceFile);
          this.copyFile(sourceFile, targetFile);
          return null;
        });
  }

  private Path computeTargetFilePath(
      BackupConfig config, String remoteFileSystemStoragePath, Path dataDir, Path filePath) {
    Path relativeFilePath = dataDir.relativize(filePath);
    String key = BackupPath.create(config, relativeFilePath.toString());
    return hostConnection.getHostControlSystem().getPath(remoteFileSystemStoragePath, key);
  }

  @Override
  public void close() throws Exception {
    this.hostConnection.getHostControlSystem().close();
  }

  /**
   * Wrapper used to copy a file
   *
   * @param source
   * @param target
   * @throws IOException
   */
  @VisibleForTesting
  void copyFile(Path source, Path target) throws IOException {
    MoreFiles.copyLarge(source, target);
  }
}
