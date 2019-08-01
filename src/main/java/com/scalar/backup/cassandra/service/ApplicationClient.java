package com.scalar.backup.cassandra.service;

public interface ApplicationClient extends AutoCloseable {

  void pause();

  void unpause();
}
