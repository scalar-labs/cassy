package com.scalar.cassy.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.palantir.giraffe.file.MoreFiles;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.service.AwsS3RestoreModule;
import com.scalar.cassy.service.AzureBlobRestoreModule;
import com.scalar.cassy.service.RestoreService;
import com.scalar.cassy.transferer.BackupPath;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class RestoreCommand extends AbstractCommand {
  private static final Logger logger = LoggerFactory.getLogger(RestoreCommand.class);

  @CommandLine.Option(
      names = {"--restore-type"},
      required = true,
      paramLabel = "RESTORE_TYPE",
      description = "The type of restore to perform")
  private int restoreType;

  @CommandLine.Option(
      names = {"--snapshot-only"},
      description = "A flag to specify restoring snapshots only")
  private boolean snapshotOnly = false;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new RestoreCommand()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Void call() throws Exception {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, clusterId);
    props.setProperty(RestoreConfig.SNAPSHOT_ID, snapshotId);
    props.setProperty(RestoreConfig.TARGET_IP, targetIp);
    props.setProperty(RestoreConfig.DATA_DIR, dataDir);
    props.setProperty(RestoreConfig.STORE_BASE_URI, storeBaseUri);
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(restoreType));
    props.setProperty(RestoreConfig.SNAPSHOT_ONLY, Boolean.toString(snapshotOnly));

    Injector injector;
    switch (storeType) {
      case AWS_S3:
        injector = Guice.createInjector(new AwsS3RestoreModule());
        break;
      case AZURE_BLOB:
        injector = Guice.createInjector(new AzureBlobRestoreModule(storeBaseUri));
        break;
      default:
        throw new UnsupportedOperationException(
            "The storage type " + storeType + " is not implemented");
    }

    try (RestoreService service = injector.getInstance(RestoreService.class)) {
      Arrays.asList(keyspaces.split(","))
          .forEach(
              k -> {
                props.setProperty(RestoreConfig.KEYSPACE, k);
                service.restore(new RestoreConfig(props));
                // TODO reporting
              });
    } finally {
      deleteStagingDirectories(props);
    }

    return null;
  }

  private void deleteStagingDirectories(Properties props) throws IOException {
    RestoreConfig config = new RestoreConfig(props);
    String stagingDirectoryName = BackupPath.getType(config);
    Path stagingDirectory = Paths.get(config.getDataDir(), stagingDirectoryName);
    logger.debug("Delete staging directories used for restoration : " + stagingDirectory);
    MoreFiles.deleteRecursive(stagingDirectory);
  }
}
