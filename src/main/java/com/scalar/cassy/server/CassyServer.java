package com.scalar.cassy.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.rpc.CassyGrpc.CassyImplBase;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public final class CassyServer extends CassyImplBase {
  private static final Logger logger = LoggerFactory.getLogger(CassyServer.class);
  private final CassyServerConfig config;
  private io.grpc.Server server;
  private Injector injector;
  private RemoteCommandHandler remoteCommandHandler;
  private ExecutorService handlerService;

  public CassyServer(CassyServerConfig config) {
    this.config = config;
  }

  private void start() throws IOException {
    injector = Guice.createInjector(new CassyServerModule(config));
    remoteCommandHandler = injector.getInstance(RemoteCommandHandler.class);
    handlerService = Executors.newFixedThreadPool(1);
    handlerService.submit(remoteCommandHandler);

    ServerBuilder builder =
        ServerBuilder.forPort(config.getPort())
            .addService(injector.getInstance(CassyServerController.class))
            .addService(ProtoReflectionService.newInstance());

    server = builder.build().start();
    logger.info("Server started, listening on " + config.getPort());

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                  System.err.println("*** shutting down gRPC server since JVM is shutting down");
                  CassyServer.this.stop();
                  System.err.println("*** server shut down");
                }));
  }

  private void stop() {
    if (server != null) {
      try {
        server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.interrupted();
        logger.warn("CassyServer shutdown is interrupted.", e);
      }
    }
    if (handlerService != null) {
      remoteCommandHandler.stop();
      handlerService.shutdown();
      try {
        handlerService.awaitTermination(10, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.interrupted();
        logger.warn("RemoteCommandHandler shutdown is interrupted.", e);
      }
    }
  }

  /** Await termination on the main thread since the grpc library uses daemon threads. */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
      handlerService.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    CassyServerConfig config = null;
    for (int i = 0; i < args.length; ++i) {
      if ("--config".equals(args[i])) {
        config = new CassyServerConfig(new File(args[++i]));
      } else if ("-help".equals(args[i])) {
        printUsageAndExit();
      }
    }
    if (config == null) {
      printUsageAndExit();
    }

    final CassyServer server = new CassyServer(config);
    server.start();
    server.blockUntilShutdown();
  }

  private static void printUsageAndExit() {
    System.err.println("CassyServer --config backup-server.properties");
    System.exit(1);
  }
}
