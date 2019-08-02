package com.scalar.backup.cassandra.service;

public interface ApplicationPauseClient extends AutoCloseable {

  void pause();

  void unpause();
}
