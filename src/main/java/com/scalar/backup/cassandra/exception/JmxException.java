package com.scalar.backup.cassandra.exception;

public class JmxException extends RuntimeException {

  public JmxException(String message) {
    super(message);
  }

  public JmxException(Throwable cause) {
    super(cause);
  }

  public JmxException(String message, Throwable cause) {
    super(message, cause);
  }
}
