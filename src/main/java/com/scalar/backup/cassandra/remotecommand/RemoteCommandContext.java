package com.scalar.backup.cassandra.remotecommand;

import com.scalar.backup.cassandra.service.BackupKey;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RemoteCommandContext {
  private final RemoteCommand command;
  private final BackupKey backupKey;
  private final RemoteCommandFuture future;

  public RemoteCommandContext(
      RemoteCommand command, BackupKey backupKey, RemoteCommandFuture future) {
    this.command = command;
    this.backupKey = backupKey;
    this.future = future;
  }

  public RemoteCommand getCommand() {
    return command;
  }

  public BackupKey getBackupKey() {
    return backupKey;
  }

  public RemoteCommandFuture getFuture() {
    return future;
  }
}
