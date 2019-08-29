package com.scalar.cassy.remotecommand;

import com.palantir.giraffe.command.CommandResult;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RemoteCommandResult {
  private final CommandResult result;

  public RemoteCommandResult(CommandResult result) {
    this.result = result;
  }

  public int getExitStatus() {
    return result.getExitStatus();
  }

  public String getStdOut() {
    return result.getStdOut();
  }

  public String getStdErr() {
    return result.getStdErr();
  }
}
