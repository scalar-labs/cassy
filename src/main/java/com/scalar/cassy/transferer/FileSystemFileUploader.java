package com.scalar.cassy.transferer;

import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.file.MoreFiles;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileUploader implements FileUploader {

  private static final Logger logger = LoggerFactory.getLogger(FileSystemFileUploader.class);
  private final HostControlSystem remoteStorage;

  @Inject
  FileSystemFileUploader(HostControlSystem remoteStorage) {
    this.remoteStorage = remoteStorage;
  }

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    try {
      URI sshURI = URI.create(config.getStoreBaseUri());
      String storagePath = URI.create(config.getStoreBaseUri()).getPath();
      long uploadStart = System.currentTimeMillis();
      Path destDir =
          remoteStorage.getPath(storagePath, BackupPath.create(config, config.getKeyspace()));
      Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
      Files.createDirectories(remoteStorage.getPath(destDir.getParent().toString()));
      logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
      MoreFiles.copyRecursive(sourceDir, destDir);
//      Commands.execute(Commands.get("scp", "-r", sourceDir.toAbsolutePath(),
//          String.format("%s@%s:%s", sshURI.getUserInfo(), sshURI.getHost(),
//              destDir.toAbsolutePath())));
      long uploadEnd = System.currentTimeMillis();
      logger.info(
          "Upload completed in " + DurationFormatUtils
              .formatDurationWords(uploadEnd - uploadStart, true, true));
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
