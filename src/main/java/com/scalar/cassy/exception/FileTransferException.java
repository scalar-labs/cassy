package com.scalar.cassy.exception;

public class FileTransferException extends RuntimeException {

  public FileTransferException(String message) {
    super(message);
  }

  public FileTransferException(Throwable cause) {
    super(cause);
  }

  public FileTransferException(String message, Throwable cause) {
    super(message, cause);
  }
}
