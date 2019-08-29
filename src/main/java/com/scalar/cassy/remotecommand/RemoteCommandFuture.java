package com.scalar.cassy.remotecommand;

import com.palantir.giraffe.command.CommandResult;
import com.palantir.giraffe.host.HostControlSystem;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RemoteCommandFuture implements Future<RemoteCommandResult> {
  private final HostControlSystem system;
  private final Future future;

  public RemoteCommandFuture(HostControlSystem system, Future future) {
    this.system = system;
    this.future = future;
  }

  public void close() throws IOException {
    system.close();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return future.isDone();
  }

  @Override
  public RemoteCommandResult get() throws InterruptedException, ExecutionException {
    return new RemoteCommandResult((CommandResult) future.get());
  }

  @Override
  public RemoteCommandResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return new RemoteCommandResult((CommandResult) future.get(timeout, unit));
  }
}
