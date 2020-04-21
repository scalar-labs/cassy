package com.scalar.cassy.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.service.AwsS3BackupModule;
import com.scalar.cassy.service.AzureBlobBackupModule;
import com.scalar.cassy.service.BackupService;
import com.scalar.cassy.service.FileSystemBackupModule;
import java.net.URI;
import java.util.Arrays;
import java.util.Properties;
import picocli.CommandLine;

public class BackupCommand extends AbstractCommand {

  @CommandLine.Option(
      names = {"--backup-type"},
      required = true,
      paramLabel = "BACKUP_TYPE",
      description = "The type of backup to take")
  private int backupType;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new BackupCommand()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Void call() throws Exception {
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, clusterId);
    props.setProperty(BackupConfig.SNAPSHOT_ID, snapshotId);
    props.setProperty(BackupConfig.TARGET_IP, targetIp);
    props.setProperty(BackupConfig.DATA_DIR, dataDir);
    props.setProperty(BackupConfig.STORE_BASE_URI, storeBaseUri);
    props.setProperty(BackupConfig.BACKUP_TYPE, Integer.toString(backupType));

    BackupType type = BackupType.getByType(backupType);

    Injector injector;
    switch (storeType) {
      case AWS_S3:
        injector =
            Guice.createInjector(new AwsS3BackupModule(type, dataDir, snapshotId, storeBaseUri));
        break;
      case AZURE_BLOB:
        injector =
            Guice.createInjector(
                new AzureBlobBackupModule(type, dataDir, snapshotId, storeBaseUri));
        break;
      case REMOTE_FILE_SYSTEM:
        injector =
            Guice.createInjector(
                new FileSystemBackupModule(type, dataDir, snapshotId, URI.create(storeBaseUri)));
        break;
      default:
        throw new UnsupportedOperationException(
            "The storage type " + storeType + " is not implemented");
    }

    try (BackupService service = injector.getInstance(BackupService.class)) {
      Arrays.asList(keyspaces.split(","))
          .forEach(
              k -> {
                props.setProperty(BackupConfig.KEYSPACE, k);
                service.backup(new BackupConfig(props));
              });
    }

    return null;
  }
}
