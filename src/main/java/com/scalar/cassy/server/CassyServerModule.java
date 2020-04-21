package com.scalar.cassy.server;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.config.StorageType;
import com.scalar.cassy.remotecommand.RemoteCommandContext;
import com.scalar.cassy.transferer.AwsS3FileUploader;
import com.scalar.cassy.transferer.AzureBlobFileUploader;
import com.scalar.cassy.transferer.FileSystemFileUploader;
import com.scalar.cassy.transferer.FileUploader;
import com.scalar.cassy.util.AzureUtil;
import com.scalar.cassy.util.GiraffeUtil;
import java.io.IOException;
import java.net.URI;
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
  protected void configure() {
    if (config.getStorageType().equals(StorageType.AWS_S3)) {
      bind(FileUploader.class).to(AwsS3FileUploader.class).in(Singleton.class);
    } else if (config.getStorageType().equals(StorageType.AZURE_BLOB)) {
      bind(FileUploader.class).to(AzureBlobFileUploader.class).in(Singleton.class);
    } else if (config.getStorageType().equals(StorageType.REMOTE_FILE_SYSTEM)) {
      bind(FileUploader.class).to(FileSystemFileUploader.class).in(Singleton.class);
    } else {
      throw new UnsupportedOperationException(
          "The storage type " + config.getStorageType() + " is not implemented");
    }
  }

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

  @Provides
  @Singleton
  TransferManager provideTransferManager() {
    return TransferManagerBuilder.standard().build();
  }

  @Provides
  @Singleton
  AmazonS3 provideAmazonS3() {
    return AmazonS3ClientBuilder.defaultClient();
  }

  @Provides
  @Singleton
  AmazonS3URI provideAmazonS3URI() {
    return new AmazonS3URI(config.getStorageBaseUri());
  }

  @Provides
  @Singleton
  public BlobContainerAsyncClient provideBlobContainerAsyncClient() {
    return AzureUtil.getBlobContainerAsyncClient(config.getStorageBaseUri());
  }

  @Provides
  @Singleton
  public HostControlSystem provideHostControlSystem() throws IOException {
    return GiraffeUtil.openSshConnection(URI.create(config.getStorageBaseUri()));
  }
}
