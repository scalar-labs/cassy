package com.scalar.cassy.util;

import com.google.common.base.Preconditions;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class RemoteFileSystemConnection {
  private final HostControlSystem hostControlSystem;
  private final String storagePath;

  public RemoteFileSystemConnection(URI storageBaseURI) throws IOException {
    SshHostAccessor sshHostAccessor = initSshHostAccessor(storageBaseURI);
    this.hostControlSystem = sshHostAccessor.open();
    this.storagePath = storageBaseURI.getPath();
  }

  private SshHostAccessor initSshHostAccessor(URI storageBaseURI) throws IOException {
    Preconditions.checkArgument(storageBaseURI.getScheme().equals("ssh"));
    String userName =
        storageBaseURI.getUserInfo() != null
            ? storageBaseURI.getUserInfo()
            : System.getProperty("user.name");
    SshCredential credential =
        PublicKeySshCredential.fromFile(
            userName, Paths.get(System.getProperty("user.home"), ".ssh/id_rsa"));
    return SshHostAccessor.forCredential(
        Host.fromHostname(storageBaseURI.getHost()), 22, credential);
  }

  public HostControlSystem getHostControlSystem() {
    return hostControlSystem;
  }

  public String getStoragePath() {
    return storagePath;
  }
}
