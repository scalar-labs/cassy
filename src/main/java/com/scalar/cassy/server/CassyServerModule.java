package com.scalar.cassy.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CassyServerModule extends AbstractModule {
  private final CassyServerConfig config;

  public CassyServerModule(CassyServerConfig config) {
    this.config = config;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  CassyServerConfig provideCassyServerConfig() {
    return config;
  }

  @Provides
  @Singleton
  Connection provideConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(config.getMetadataDbUrl());
    connection.setAutoCommit(true);
    return connection;
  }

  @Provides
  @Singleton
  BlockingQueue<RemoteCommandContext> provideRemoteCommandQueue() {
    return new LinkedBlockingQueue<RemoteCommandContext>();
  }
}
