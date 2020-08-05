package com.scalar.cassy;

import com.scalar.cassy.rpc.CassyGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class CassyClient {
  private final CassyGrpc.CassyBlockingStub blockingStub;

  public CassyClient() {
    this(ManagedChannelBuilder.forAddress("localhost", 20051).usePlaintext());
  }

  public CassyClient(ManagedChannelBuilder<?> channelBuilder) {
    Channel channel = channelBuilder.build();
    blockingStub = CassyGrpc.newBlockingStub(channel);
  }

  public CassyGrpc.CassyBlockingStub getBlockingStub() {
    return blockingStub;
  }
}
