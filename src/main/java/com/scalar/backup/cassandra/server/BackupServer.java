package com.scalar.backup.cassandra.server;

import com.palantir.giraffe.ssh.PublicKeySshCredential;
import com.palantir.giraffe.ssh.SshCredential;
import com.scalar.backup.cassandra.config.BackupServerConfig;
import com.scalar.backup.cassandra.jmx.JmxManager;
import com.scalar.backup.cassandra.rpc.CassandraBackupGrpc;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class BackupServer extends CassandraBackupGrpc.CassandraBackupImplBase {
  private static final Logger logger = Logger.getLogger(BackupServer.class.getName());
  private io.grpc.Server server;
  private final BackupServerConfig config;

  public BackupServer(BackupServerConfig config) {
    this.config = config;
  }

  private void start() throws IOException {
    JmxManager jmx = new JmxManager(config.getCassandraHost(), config.getJmxPort());
    SshCredential credential =
        PublicKeySshCredential.fromFile(
            config.getUserName(), Paths.get(config.getPrivateKeyPath()));

    ServerBuilder builder =
        ServerBuilder.forPort(config.getPort())
            .addService(new BackupServerController(config, jmx, credential))
            .addService(ProtoReflectionService.newInstance());

    server = builder.build().start();
    logger.info("Server started, listening on " + config.getPort());

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
    BackupServerConfig config = null;
    for (int i = 0; i < args.length; ++i) {
      if ("--config".equals(args[i])) {
        config = new BackupServerConfig(new File(args[++i]));
      } else if ("-help".equals(args[i])) {
        printUsageAndExit();
      }
    }
    if (config == null) {
      printUsageAndExit();
    }

    final BackupServer server = new BackupServer(config);
    server.start();
    server.blockUntilShutdown();
  }

  private static void printUsageAndExit() {
    System.err.println("BackupServer --config properties.prop");
    System.exit(1);
  }
}
