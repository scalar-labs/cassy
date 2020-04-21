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

public class GiraffeUtil {
  public static HostControlSystem openSshConnection(URI sshURI) throws IOException {
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
