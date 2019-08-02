package com.scalar.backup.cassandra.exception;

public class PauseException extends RuntimeException {

  public PauseException(String message) {
    super(message);
  }

  public PauseException(Throwable cause) {
    super(cause);
  }

  public PauseException(String message, Throwable cause) {
    super(message, cause);
  }
}
