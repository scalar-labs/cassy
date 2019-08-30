package com.scalar.cassy.exception;

public class FileIOException extends RuntimeException {

  public FileIOException(String message) {
    super(message);
  }

  public FileIOException(Throwable cause) {
    super(cause);
  }

  public FileIOException(String message, Throwable cause) {
    super(message, cause);
  }
}
