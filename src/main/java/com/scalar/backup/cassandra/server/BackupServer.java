package com.scalar.backup.cassandra.server;

import com.google.inject.Inject;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.logging.Logger;

public class BackupServer extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServer.class.getName());
  private io.grpc.Server server;
  private int port = 20051;

  @Inject
  public BackupServer() {}

  private void start() throws IOException {

    ServerBuilder builder =
        ServerBuilder.forPort(port)
            .addService(new BackupServerService())
            .addService(ProtoReflectionService.newInstance());

    server = builder.build().start();
    logger.info("Server started, listening on " + port);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                  System.err.println("*** shutting down gRPC server since JVM is shutting down");
                  BackupServer.this.stop();
                  System.err.println("*** server shut down");
                }));
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /** Await termination on the main thread since the grpc library uses daemon threads. */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final BackupServer server = new BackupServer();
    server.start();
    server.blockUntilShutdown();
  }
}
