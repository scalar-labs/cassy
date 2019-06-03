package com.scalar.backup.cassandra.command;

import java.util.concurrent.Callable;
import picocli.CommandLine;

public abstract class AbstractCommand implements Callable<Void> {

  @CommandLine.Option(
      names = {"--cluster-id"},
      required = true,
      paramLabel = "CLUSTER_ID",
      description = "A cluster name of a Cassandra cluster")
  protected String clusterId;

  @CommandLine.Option(
      names = {"--backup-id"},
      required = true,
      paramLabel = "BACKUP_ID",
      description = "An ID of a backup to use")
  protected String backupId;

  @CommandLine.Option(
      names = {"--target-ip"},
      required = true,
      paramLabel = "TARGET_IP",
      description = "An ip of a node to operate")
  protected String targetIp;

  @CommandLine.Option(
      names = {"--data-dir"},
      required = true,
      paramLabel = "DATA_DIR",
      description = "A data directory to take backups from or restore backup to")
  protected String dataDir;

  @CommandLine.Option(
      names = {"--store-base-uri"},
      required = true,
      paramLabel = "STORE_BASE_URI",
      description = "A URI of a store to save backup files")
  protected String storeBaseUri;

  @CommandLine.Option(
      names = {"--keyspaces"},
      required = true,
      paramLabel = "KEYSPACES",
      description = "A comma-separated list of keyspaces to operate")
  protected String keyspaces;

  @CommandLine.Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "display a help message")
  protected boolean helpRequested;
}
