package com.scalar.backup.cassandra.service;

import com.google.protobuf.Empty;
import com.scalar.backup.cassandra.exception.PauseException;
import com.scalar.backup.cassandra.rpc.AdminGrpc;
import com.scalar.backup.cassandra.rpc.PauseRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcApplicationClient implements ApplicationClient {
  private static final Logger logger = LoggerFactory.getLogger(GrpcApplicationClient.class);
  private final ManagedChannel channel;
  private final AdminGrpc.AdminBlockingStub blockingStub;

  public GrpcApplicationClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  GrpcApplicationClient(ManagedChannel channel) {
    this.channel = channel;
    this.blockingStub = AdminGrpc.newBlockingStub(channel);
  }

  @Override
  public void pause() {
    PauseRequest request = PauseRequest.newBuilder().setWaitOutstanding(true).build();
    try {
      blockingStub.pause(request);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new PauseException(e);
    }
  }

  @Override
  public void unpause() {
    try {
      blockingStub.unpause(Empty.newBuilder().build());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new PauseException(e);
    }
  }

  @Override
  public void close() {
    try {
      channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new PauseException(e);
    }
  }
}
