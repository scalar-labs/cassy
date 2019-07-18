package com.scalar.backup.cassandra.server;

import com.google.common.util.concurrent.Uninterruptibles;
import com.scalar.backup.cassandra.db.DatabaseAccessor;
import com.scalar.backup.cassandra.exception.RemoteExecutionException;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandContext;
import com.scalar.backup.cassandra.remotecommand.RemoteCommandResult;
import com.scalar.backup.cassandra.rpc.OperationStatus;
import com.scalar.backup.cassandra.service.BackupServiceMaster;
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
        updateStatus(future);
      } catch (Exception e) {
        logger.warn(e.getMessage(), e);
        if (future != null) {
          updateStatus(future);
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

  private void updateStatus(RemoteCommandContext future) {
    if (future.getCommand().getName().equals(BackupServiceMaster.BACKUP_COMMAND)) {
      database.getBackupHistory().update(future.getBackupKey(), OperationStatus.COMPLETED);
    } else {
      database.getRestoreHistory().update(future.getBackupKey(), OperationStatus.COMPLETED);
    }
  }
}
