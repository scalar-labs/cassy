package com.scalar.cassy.service;

import com.google.common.base.Preconditions;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.cassy.config.BaseConfig;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class SshConnectionBuilder {
  public static HostControlSystem openSshConnection(URI sshURI) throws IOException {
    Preconditions.checkArgument(sshURI.getScheme().equals("ssh"));
    String userName =
        sshURI.getUserInfo() != null ? sshURI.getUserInfo() : System.getProperty("user.name");
    int port = sshURI.getPort() != -1 ? sshURI.getPort() : 22;
    SshCredential credential = PublicKeySshCredential.fromFile(userName,
        Paths.get(System.getProperty("user.home"), ".ssh/id_rsa"));
    SshHostAccessor sshHostAccessor =
        SshHostAccessor.forCredential(Host.fromHostname(sshURI.getHost()), port, credential);
    return sshHostAccessor.open();
  }
}
