package com.scalar.cassy.transferer;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileIOException;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsS3FileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AwsS3FileUploader.class);
  private static final int NUM_THREADS = 3;
  private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
  private final TransferManager manager;
  private final AmazonS3 s3;
  private final AmazonS3URI s3Uri;

  @Inject
  public AwsS3FileUploader(TransferManager manager, AmazonS3 s3, AmazonS3URI s3Uri) {
    this.manager = manager;
    this.s3 = s3;
    this.s3Uri = s3Uri;
  }

  @Override
  public Future<Void> upload(Path file, String key, String storageBaseUri) {
    if (!requiresUpload(s3Uri.getBucket(), key, file)) {
      logger.info(file + " has been already uploaded.");
      return CompletableFuture.completedFuture(null);
    }

    logger.info("Uploading " + file);
    return executorService.submit(
        () -> {
          manager.upload(s3Uri.getBucket(), key, file.toFile()).waitForCompletion();
          return null;
        });
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    Path dataDir = Paths.get(config.getDataDir());

    List<Upload> uploads = new ArrayList<>();
    files.forEach(
        p -> {
          Path relative = dataDir.relativize(p);
          String key = BackupPath.create(config, relative.toString());
          if (requiresUpload(s3Uri.getBucket(), key, p)) {
            logger.info("Uploading " + p);
            try {
              uploads.add(manager.upload(s3Uri.getBucket(), key, p.toFile()));
            } catch (RuntimeException e) {
              throw new FileTransferException(e);
            }
          } else {
            logger.info(p + " has been already uploaded.");
          }
        });

    uploads.forEach(
        u -> {
          try {
            u.waitForCompletion();
            logger.info(u.getDescription() + " - " + u.getState().name());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileTransferException(e);
          }
        });
  }

  @Override
  public void close() {
    manager.shutdownNow();
  }

  @VisibleForTesting
  boolean requiresUpload(String bucket, String key, Path file) {
    try {
      if (s3.getObjectMetadata(bucket, key).getContentLength() == Files.size(file)) {
        return false;
      }
    } catch (IOException e) {
      throw new FileIOException(e);
    } catch (AmazonServiceException e) {
      // if the file doesn't exist in the bucket, it will be uploaded
      if (e.getStatusCode() == 404) {
        return true;
      }
      throw new FileTransferException(e);
    }
    return true;
  }
}
