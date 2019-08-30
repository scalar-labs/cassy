package com.scalar.cassy.exception;

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
