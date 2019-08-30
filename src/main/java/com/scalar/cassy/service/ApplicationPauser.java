package com.scalar.cassy.service;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.exception.PauseException;
import com.scalar.cassy.exception.TimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

@Immutable
public class ApplicationPauser {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationPauser.class);
  private final Optional<String> srvServiceUrl;

  public ApplicationPauser(Optional<String> srvServiceUrl) {
    this.srvServiceUrl = srvServiceUrl;
  }

  public void pause() {
    run(client -> client.pause());
  }

  public void unpause() {
    run(client -> client.unpause());
  }

  private void run(Consumer<ApplicationPauseClient> consumer) {
    String serviceUrl =
        srvServiceUrl.orElseThrow(
            () -> new PauseException("Please configure srv_service_url to pause a cluster. "));

    // Assume that the list of addresses for unpause is the same as the one for pause.
    List<SRVRecord> records = getApplicationIps(serviceUrl);

    List<Future<String>> futures = new ArrayList<>();
    ExecutorService executor = Executors.newCachedThreadPool();
    records.forEach(
        record -> {
          // use Callable to propagate exceptions
          Callable<String> task =
              () -> {
                try (ApplicationPauseClient client =
                    getClient(record.getTarget().toString(true), record.getPort())) {
                  consumer.accept(client);
                } catch (Exception e) {
                  logger.error(e.getMessage(), e);
                  throw new PauseException(e);
                }
                return null;
              };
          futures.add(executor.submit(task));
        });

    executor.shutdown();
    try {
      for (Future f : futures) {
        f.get();
      }
      boolean terminated = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
      if (!terminated) {
        throw new TimeoutException("timeout occurred in pause or unpause.");
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new PauseException(e);
    }
  }

  @VisibleForTesting
  List<SRVRecord> getApplicationIps(String srvServiceUrl) {
    Record[] records;
    try {
      records = new Lookup(srvServiceUrl, Type.SRV).run();
      if (records == null) {
        throw new PauseException("Can't get SRV records from " + srvServiceUrl);
      }
    } catch (TextParseException e) {
      logger.error(e.getMessage(), e);
      throw new PauseException(e);
    }

    List<SRVRecord> srvRecords = new ArrayList<>();
    for (int i = 0; i < records.length; i++) {
      srvRecords.add((SRVRecord) records[i]);
    }
    return srvRecords;
  }

  @VisibleForTesting
  ApplicationPauseClient getClient(String host, int port) {
    return new GrpcApplicationPauseClient(host, port);
  }
}
