package com.scalar.backup.cassandra.transferer;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.exception.FileIOException;
import com.scalar.backup.cassandra.exception.FileTransferException;
import com.scalar.backup.cassandra.traverser.FileTraverser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsS3FileUploader implements FileUploader {
  private static final Logger logger = LoggerFactory.getLogger(AwsS3FileUploader.class);
  private final FileTraverser traverser;
  private final TransferManager manager;
  private final AmazonS3 s3;

  @Inject
  public AwsS3FileUploader(FileTraverser traverser, TransferManager manager, AmazonS3 s3) {
    this.traverser = traverser;
    this.manager = manager;
    this.s3 = s3;
  }

  @Override
  public void upload(BackupConfig config) {
    AmazonS3URI s3Uri = new AmazonS3URI(config.getStoreBaseUri());
    List<Path> files = traverser.traverse(config.getKeyspace());

    List<Upload> uploads = new ArrayList<>();
    files.forEach(
        p -> {
          String key = BackupPath.create(config, p.toString());
          Path file = Paths.get(config.getDataDir(), p.toString());
          if (requiresUpload(s3Uri.getBucket(), key, file)) {
            logger.info("Uploading " + file);
            try {
              uploads.add(manager.upload(s3Uri.getBucket(), key, file.toFile()));
            } catch (RuntimeException e) {
              throw new FileTransferException(e);
            }
          } else {
            logger.info(file + " has been already uploaded.");
          }
        });

    uploads.forEach(
        u -> {
          try {
            u.waitForCompletion();
          } catch (InterruptedException e) {
            throw new FileTransferException(e);
          }
        });
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
