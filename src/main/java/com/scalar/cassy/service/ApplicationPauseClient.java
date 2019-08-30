package com.scalar.cassy.service;

public interface ApplicationPauseClient extends AutoCloseable {

  void pause();

  void unpause();
}
