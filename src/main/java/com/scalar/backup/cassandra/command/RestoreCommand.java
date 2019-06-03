package com.scalar.backup.cassandra.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.service.AwsS3RestoreModule;
import com.scalar.backup.cassandra.service.RestoreService;
import java.util.Arrays;
import java.util.Properties;
import picocli.CommandLine;

public class RestoreCommand extends AbstractCommand {

  @CommandLine.Option(
      names = {"--restore-type"},
      required = true,
      paramLabel = "RESTORE_TYPE",
      description = "A type of restore to take")
  private int restoreType;

  public static void main(String[] args) {
    CommandLine.call(new RestoreCommand(), args);
  }

  @Override
  public Void call() throws Exception {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, clusterId);
    props.setProperty(RestoreConfig.BACKUP_ID, backupId);
    props.setProperty(RestoreConfig.TARGET_IP, targetIp);
    props.setProperty(RestoreConfig.DATA_DIR, dataDir);
    props.setProperty(RestoreConfig.STORE_BASE_URI, storeBaseUri);
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(restoreType));

    // TODO: switching modules depending on the specified store_type
    Injector injector = Guice.createInjector(new AwsS3RestoreModule());

    try (RestoreService service = injector.getInstance(RestoreService.class)) {
      Arrays.asList(keyspaces.split(","))
          .forEach(
              k -> {
                props.setProperty(RestoreConfig.KEYSPACE, k);
                service.restore(new RestoreConfig(props));
                // TODO reporting
              });
    }

    return null;
  }
}
