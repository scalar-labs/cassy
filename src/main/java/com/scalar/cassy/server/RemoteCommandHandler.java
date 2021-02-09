package com.scalar.cassy.server;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import com.scalar.cassy.db.BackupHistory;
import com.scalar.cassy.db.RestoreHistory;
import com.scalar.cassy.exception.RemoteExecutionException;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandResult;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.service.BackupServiceMaster;
import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(RemoteCommandHandler.class);
  private final BlockingQueue<RemoteCommandContext> futures;
  private final Connection connection;
  private final BackupHistory backupHistory;
  private final RestoreHistory restoreHistory;

  @Inject
  public RemoteCommandHandler(BlockingQueue<RemoteCommandContext> futures, Connection connection) {
    this.futures = futures;
    this.connection = connection;
    this.backupHistory = new BackupHistory(connection);
    this.restoreHistory = new RestoreHistory(connection);
  }

  @Override
  public void run() {
    while (true) {
      RemoteCommandContext future = null;
      try {
        future = futures.peek();
        if (future == null) {
          Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
          continue;
        }
        RemoteCommandResult result = Uninterruptibles.getUninterruptibly(future.getFuture());
        if (result.getExitStatus() != 0) {
          throw new RemoteExecutionException(
              future.getCommand().getCommand() + " failed for some reason");
        }
        updateStatus(future, OperationStatus.COMPLETED);
      } catch (Exception e) {
        logger.warn(e.getMessage(), e);
        if (future != null) {
          updateStatus(future, OperationStatus.FAILED);
        }
      } finally {
        if (future != null) {
          try {
            future.getFuture().close();
          } catch (IOException e) {
            // ignore
          }
          futures.remove();
        }
      }
    }
  }

  private void updateStatus(RemoteCommandContext future, OperationStatus status) {
    if (future.getCommand().getName().equals(BackupServiceMaster.BACKUP_COMMAND)) {
      backupHistory.update(future.getBackupKey(), status);
    } else {
      restoreHistory.update(future.getBackupKey(), status);
    }
  }
}
