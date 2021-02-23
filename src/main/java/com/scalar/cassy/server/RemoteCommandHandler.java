package com.scalar.cassy.server;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.db.BackupHistory;
import com.scalar.cassy.db.RestoreHistory;
import com.scalar.cassy.exception.RemoteExecutionException;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.remotecommand.RemoteCommandResult;
import com.scalar.cassy.rpc.OperationStatus;
import com.scalar.cassy.service.BackupServiceMaster;
import com.scalar.cassy.util.ConnectionUtil;
import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCommandHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(RemoteCommandHandler.class);
  private final CassyServerConfig config;
  private final BlockingQueue<RemoteCommandContext> futures;
  private boolean isActive = true;

  @Inject
  public RemoteCommandHandler(
      CassyServerConfig config, BlockingQueue<RemoteCommandContext> futures) {
    this.config = config;
    this.futures = futures;
  }

  @Override
  public void run() {
    while (isActive) {
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

  public void stop() {
    isActive = false;
  }

  private void updateStatus(RemoteCommandContext future, OperationStatus status) {
    Connection connection = null;
    try {
      connection = ConnectionUtil.create(config.getMetadataDbUrl());
      if (future.getCommand().getName().equals(BackupServiceMaster.BACKUP_COMMAND)) {
        new BackupHistory(connection).update(future.getBackupKey(), status);
      } else {
        new RestoreHistory(connection).update(future.getBackupKey(), status);
      }
    } catch (Exception e) {
      logger.error(
          "Writing status " + status + " for " + future.getCommand().getName() + " failed.", e);
    } finally {
      ConnectionUtil.close(connection);
    }
  }
}
