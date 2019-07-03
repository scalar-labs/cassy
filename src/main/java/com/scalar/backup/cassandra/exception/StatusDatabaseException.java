package com.scalar.backup.cassandra.exception;

public class StatusDatabaseException extends RuntimeException {

  public StatusDatabaseException(String message) {
    super(message);
  }

  public StatusDatabaseException(Throwable cause) {
    super(cause);
  }

  public StatusDatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
