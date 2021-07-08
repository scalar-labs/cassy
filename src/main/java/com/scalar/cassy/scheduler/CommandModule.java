package com.scalar.cassy.scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.cassy.rpc.CassyGrpc;
import io.grpc.ManagedChannelBuilder;

public class CommandModule extends AbstractModule {
  @Provides
  @Singleton
  CassyClient provideCassyClient() {
    CassyGrpc.CassyBlockingStub blockingStub =
        CassyGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress(
                    System.getenv().getOrDefault("CASSY_SCHEDULER_HOST", "localhost"),
                    Integer.parseInt(System.getenv().getOrDefault("CASSY_SCHEDULER_PORT", "20051")))
                .usePlaintext()
                .build());

    return new CassyClient(blockingStub);
  }
}
