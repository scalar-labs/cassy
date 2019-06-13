package com.scalar.backup.cassandra.service;

import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.CommandFuture;
import com.palantir.giraffe.command.CommandResult;
import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.host.Host;
import com.palantir.giraffe.host.HostControlSystem;
import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.palantir.giraffe.ssh.SshHostAccessor;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandExecutor {
  private static final Logger logger =
      LoggerFactory.getLogger(RemoteCommandExecutor.class.getName());

  public void execute(RemoteCommand command) {
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

    try (HostControlSystem hcs = ssh.open()) {
      Command remoteCommand =
          hcs.getExecutionSystem()
              .getCommandBuilder(command.getCommand())
              .addArguments(command.getArguments())
              .build();
      logger.info("executing " + remoteCommand + " in " + command.getIp());
      CommandFuture future = Commands.executeAsync(remoteCommand);

      CommandResult result = Commands.waitFor(future);
      if (result.getExitStatus() != 0) {
        logger.error(command.getName() + " failed for some reason");
        throw new RuntimeException(command.getName() + " failed for some reason");
      }
      logger.debug(result.getStdOut());
      logger.warn(result.getStdErr());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new RemoteExecutionException(e);
    }
  }
}
