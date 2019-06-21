package com.scalar.backup.cassandra.exception;

public class PlacementException extends RuntimeException {

  public PlacementException(String message) {
    super(message);
  }

  public PlacementException(Throwable cause) {
    super(cause);
  }

  public PlacementException(String message, Throwable cause) {
    super(message, cause);
  }
}
