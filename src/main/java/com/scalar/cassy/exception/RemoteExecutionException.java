package com.scalar.cassy.exception;

public class RemoteExecutionException extends RuntimeException {

  public RemoteExecutionException(String message) {
    super(message);
  }

  public RemoteExecutionException(Throwable cause) {
    super(cause);
  }

  public RemoteExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
