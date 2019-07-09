package com.scalar.backup.cassandra.server;

import com.google.common.util.concurrent.Uninterruptibles;
import com.scalar.backup.cassandra.db.BackupHistory;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandContext;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandResult;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(RemoteCommandContext.class);
  private final BlockingQueue<RemoteCommandContext> futures;
  private final BackupHistory history;

  public RemoteCommandHandler(BlockingQueue<RemoteCommandContext> futures, BackupHistory history) {
    this.futures = futures;
    this.history = history;
  }

  @Override
  public void run() {
    while (true) {
      RemoteCommandContext future = null;
      try {
        future = futures.take();
        RemoteCommandResult result = Uninterruptibles.getUninterruptibly(future.getFuture());
        if (result.getExitStatus() != 0) {
          throw new RemoteExecutionException(
              future.getCommand().getCommand() + " failed for some reason");
        }
        if (future.getCommand().getName().equals(BackupServiceMaster.BACKUP_COMMAND)) {
          history.update(future.getBackupKey(), OperationStatus.COMPLETED);
        } else {
          // TODO: coming in a later PR
        }
      } catch (Exception e) {
        logger.warn(e.getMessage(), e);
        if (future != null) {
          history.update(future.getBackupKey(), OperationStatus.FAILED);
        }
      }
    }
  }
}
