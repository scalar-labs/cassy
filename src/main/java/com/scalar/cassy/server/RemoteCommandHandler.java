package com.scalar.cassy.server;

import com.google.common.util.concurrent.Uninterruptibles;
import com.scalar.cassy.db.DatabaseAccessor;
import com.scalar.cassy.exception.RemoteExecutionException;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandResult;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.service.BackupServiceMaster;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(RemoteCommandHandler.class);
  private final BlockingQueue<RemoteCommandContext> futures;
  private final DatabaseAccessor database;

  public RemoteCommandHandler(
      BlockingQueue<RemoteCommandContext> futures, DatabaseAccessor database) {
    this.futures = futures;
    this.database = database;
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
        }
      }
    }
  }

  private void updateStatus(RemoteCommandContext future, OperationStatus status) {
    if (future.getCommand().getName().equals(BackupServiceMaster.BACKUP_COMMAND)) {
      database.getBackupHistory().update(future.getBackupKey(), status);
    } else {
      database.getRestoreHistory().update(future.getBackupKey(), status);
    }
  }
}
