package com.scalar.cassy.remotecommand;

import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.cassy.exception.RemoteExecutionException;
import java.io.IOException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandExecutor {
  private static final Logger logger =
      LoggerFactory.getLogger(RemoteCommandExecutor.class.getName());

  public RemoteCommandFuture execute(RemoteCommand command) {
    SshCredential credential = null;
    try {
      credential =
          PublicKeySshCredential.fromFile(command.getUsername(), command.getPrivateKeyFile());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new RemoteExecutionException(e);
    }
    SshHostAccessor ssh =
        SshHostAccessor.forCredential(Host.fromHostname(command.getIp()), credential);

    HostControlSystem hcs = null;
    try {
      hcs = ssh.open();
      Command remoteCommand =
          hcs.getExecutionSystem()
              .getCommandBuilder(command.getCommand())
              .addArguments(command.getArguments())
              .build();
      logger.info("executing " + remoteCommand + " in " + command.getIp());

      Future future = Commands.executeAsync(remoteCommand);
      return new RemoteCommandFuture(hcs, future);
    } catch (IOException e) {
      try {
        if (hcs != null) {
          hcs.close();
        }
      } catch (IOException e1) {
        logger.error(e.getMessage(), e1);
      }
      logger.error(e.getMessage(), e);
      throw new RemoteExecutionException(e);
    }
  }
}
