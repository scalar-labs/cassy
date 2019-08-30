package com.scalar.cassy.exception;

public class FileTraversalException extends RuntimeException {

  public FileTraversalException(String message) {
    super(message);
  }

  public FileTraversalException(String message, Throwable cause) {
    super(message, cause);
  }
}
