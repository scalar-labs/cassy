package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.Commands;
import com.scalar.cassy.config.BaseConfig;
import java.io.IOException;
import java.net.URI;

public class FileSystemTransfererBase {
  /**
   * Wrapper used to execute a command. It was created to facilitate testing.
   *
   * @param command a command
   * @throws IOException
   */
  @VisibleForTesting
  void executeCommand(Command command) throws IOException {
    Commands.execute(command);
  }

  String getHost(BaseConfig config) {
    URI remoteStorageURI = URI.create(config.getStoreBaseUri());
    String userName =
        remoteStorageURI.getUserInfo() != null
            ? remoteStorageURI.getUserInfo()
            : System.getProperty("user.name");
    return String.format("%s@%s", userName, remoteStorageURI.getHost());
  }
}
