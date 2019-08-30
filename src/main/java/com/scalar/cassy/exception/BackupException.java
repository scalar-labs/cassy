package com.scalar.cassy.exception;

public class BackupException extends RuntimeException {

  public BackupException(String message) {
    super(message);
  }

  public BackupException(Throwable cause) {
    super(cause);
  }

  public BackupException(String message, Throwable cause) {
    super(message, cause);
  }
}
