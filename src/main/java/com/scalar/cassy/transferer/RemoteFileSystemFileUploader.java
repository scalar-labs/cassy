package com.scalar.cassy.transferer;

import com.palantir.giraffe.file.MoreFiles;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.cassy.config.BackupConfig;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteFileSystemFileUploader implements FileUploader {

  private static final Logger logger = LoggerFactory.getLogger(RemoteFileSystemFileUploader.class);

//  @Override
//  public void upload(RestoreConfig config) throws IOException {
//    String key = BackupPath.create(config, config.getKeyspace());
//
//    SshCredential credential = PublicKeySshCredential.of("vincent", "");
//    SshHostAccessor ssh =
//        SshHostAccessor.forCredential(Host.fromHostname("172.16.1.23"), credential);
////
////    Path sourceFile = Paths.get("/Users/vguilpain/dev/sandbox/cassy/Dockerfile");
////    String remoteDir = "/home/vincent/Downloads/test";
//
//    try (HostControlSystem hcs = ssh.open()) {
//      Path remoteDirPath = hcs.getPath(remoteDir, sourceFile.getName(sourceFile.getNameCount()-1).toString());
//      MoreFiles.copyLarge(sourceFile, remoteDirPath);
//    }
//    Files.copy(source, newdir.resolve(source.getFileName());
//  }


  @Override
  public void upload(List<Path> files, BackupConfig config)  {
    SshCredential credential = null;
    try {
      credential = PublicKeySshCredential.fromFile("vincent", Paths.get("/home/vincent/.ssh/id_rsa"));
    } catch (IOException e) {
      logger.error("Could not read private key",e);
      return;
    }
    SshHostAccessor ssh =
        SshHostAccessor.forCredential(Host.fromHostname("172.16.1.23"), credential);
    String remoteDir = "/home/vincent/Downloads/test";
    try (HostControlSystem hcs = ssh.open()) {
      MoreFiles
          .copyLarge(hcs.getPath(remoteDir, "Dockerfile"), hcs.getPath(remoteDir, "Dockerfile2"));
      logger.info("Copied dockerfile");
    } catch (IOException e) {
      logger.error("Could not copy file",e);
    }
  }

  @Override
  public void close() throws Exception {

  }
}
