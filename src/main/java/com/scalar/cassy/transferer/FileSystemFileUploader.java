package com.scalar.cassy.transferer;

import com.palantir.giraffe.file.MoreFiles;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileSystemFileUploader implements FileUploader {

  @Override
  public void upload(List<Path> files, BackupConfig config) {
    try (HostControlSystem hcs = SshConnectionBuilder.openSshConnection(config)) {
      String storagePath = URI.create(config.getStoreBaseUri()).getPath();
      for (Path p : files) {
        File destFile = new File(
            hcs.getPath(storagePath, BackupPath.create(config, p.toString())).toString());
        File sourceFile = new File(Paths.get(config.getDataDir(), p.toString()).toString());
        if (!(destFile.exists() && destFile.length() == sourceFile.length())) {
          Files.createDirectories(hcs.getPath(destFile.getParent()));
          MoreFiles
              .copyLarge(sourceFile.toPath(),
                  destFile.toPath());
        }
      }
    } catch (IOException e) {
      throw new FileTransferException(e);
    }
  }

  @Override
  public void close() {
  }
}
