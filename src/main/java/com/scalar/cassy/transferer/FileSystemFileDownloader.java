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

public class FileSystemFileDownloader implements FileDownloader {

  @Override
  public void download(RestoreConfig config) {
    try (HostControlSystem hcs = SshConnectionBuilder.openSshConnection(config)) {
      String storagePath = URI.create(config.getStoreBaseUri()).getPath();
      Path backupKeyspace = hcs
          .getPath(storagePath, BackupPath.create(config, config.getKeyspace()));
      Path targetKeyspace = Paths
          .get(config.getDataDir(), BackupPath.create(config, config.getKeyspace()));
      Files.createDirectories(targetKeyspace.getParent());
      MoreFiles.copyRecursive(backupKeyspace, targetKeyspace);
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() {
  }
}
