package com.scalar.cassy.service;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.transferer.FileSystemFileUploader;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class FileSystemBackupModule extends AbstractModule {
  private final BackupType type;
  private final String dataDir;
  private final String snapshotId;
  private final URI storageURI;

  public FileSystemBackupModule(
      BackupType type, String dataDir, String snapshotId, URI storageURI) {
    this.type = type;
    this.dataDir = dataDir;
    this.snapshotId = snapshotId;
    this.storageURI = storageURI;
  }

  @Override
  protected void configure() {
    bind(FileUploader.class).to(FileSystemFileUploader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  FileTraverser provideFileTraverser() {
    if (type.equals(BackupType.NODE_INCREMENTAL)) {
      return new IncrementalBackupTraverser(Paths.get(dataDir));
    }
    return new SnapshotTraverser(Paths.get(dataDir), snapshotId);
  }

  @Provides
  HostControlSystem provideHostControlSystem() throws IOException {
    return openSshConnection(storageURI);
  }

  private HostControlSystem openSshConnection(URI sshURI) throws IOException {
    Preconditions.checkArgument(sshURI.getScheme().equals("ssh"));
    String userName =
        sshURI.getUserInfo() != null ? sshURI.getUserInfo() : System.getProperty("user.name");
    SshCredential credential =
        PublicKeySshCredential.fromFile(
            userName, Paths.get(System.getProperty("user.home"), ".ssh/id_rsa"));
    SshHostAccessor sshHostAccessor =
        SshHostAccessor.forCredential(Host.fromHostname(sshURI.getHost()), 22, credential);
    return sshHostAccessor.open();
  }
}
