package com.scalar.cassy.transferer;

import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.Commands;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileUploader implements FileUploader {

  private static final Logger logger = LoggerFactory.getLogger(FileSystemFileUploader.class);

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    try {
      String storagePath = URI.create(config.getStoreBaseUri()).getPath();
      long uploadStart = System.currentTimeMillis();
      Path destDir =
          Paths.get(storagePath, BackupPath.create(config, config.getKeyspace()));
      Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
      URI sshURI = URI.create(config.getStoreBaseUri());
      logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
      Commands.execute(
          Commands.get("ssh", getHost(sshURI), String.format("mkdir -p %s", destDir.getParent())));
//      Commands.execute(buildScpCommand(sshURI, sourceDir, destDir));
      Commands.execute( Commands.get("rsync", "-rzP",sourceDir.toAbsolutePath(),"-e ssh",
          String.format("%s:%s", getHost(sshURI),
              destDir.toAbsolutePath())));
      long uploadEnd = System.currentTimeMillis();
      logger.info(
          "Upload completed in " + DurationFormatUtils
              .formatDurationWords(uploadEnd - uploadStart, true, true));
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  //@Override
//public void upload(List<Path> files, BackupConfig config) {
//  try (HostControlSystem remoteStorage  = SshConnectionBuilder.openSshConnection(URI.create(config.getStoreBaseUri()))){
//    URI sshURI = URI.create(config.getStoreBaseUri());
//    String storagePath = URI.create(config.getStoreBaseUri()).getPath();
//    long uploadStart = System.currentTimeMillis();
//    Path destDir =
//        remoteStorage.getPath(storagePath, BackupPath.create(config, config.getKeyspace()));
//    Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
//    Files.createDirectories(remoteStorage.getPath(destDir.getParent().toString()));
//    logger.info(String.format("Uploading keyspace \"%s\"", config.getKeyspace()));
//    MoreFiles.copyRecursive(sourceDir, destDir);
//    long uploadEnd = System.currentTimeMillis();
//    logger.info(
//        "Upload completed in " + DurationFormatUtils
//            .formatDurationWords(uploadEnd - uploadStart, true, true));
//  } catch (IOException e) {
//    throw new FileTransferException(e);
//  }
//}
  private String getHost(URI sshURI) {
    String userName =
        sshURI.getUserInfo() != null ? sshURI.getUserInfo() : System.getProperty("user.name");
    return String.format("%s@%s", userName, sshURI.getHost());
  }

  private Command buildScpCommand(URI sshURI, Path sourceDir, Path destDir) {
    int port = sshURI.getPort() != -1 ? sshURI.getPort() : 22;
    return Commands.get("scp", "-r", String.format("-P %s", port), sourceDir.toAbsolutePath(),
        String.format("%s:%s", getHost(sshURI),
            destDir.toAbsolutePath()));
  }

  @Override
  public void close() throws Exception {

  }
}
