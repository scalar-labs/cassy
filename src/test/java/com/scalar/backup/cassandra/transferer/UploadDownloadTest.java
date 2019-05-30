package com.scalar.backup.cassandra.transferer;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.BaseConfig;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.config.RestoreType;
import com.scalar.backup.cassandra.traverser.SnapshotTraverser;
import java.nio.file.FileSystems;
import java.util.Properties;

// For a temporary testing. It will be deleted.
public class UploadDownloadTest {
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_BACKUP_ID = "backup_id";
  private static final String ANY_TARGET_IP = "target_ip";

  public static void main(String[] args) {
    String dataDir = "/Users/hiroyuki/packages/apache-cassandra-3.11.3/data/data";
    String keyspace = "keyspace1";
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(BackupConfig.BACKUP_ID, ANY_BACKUP_ID);
    props.setProperty(BackupConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(BackupConfig.DEST_BASE_URI, "s3://scalar-tmp");
    props.setProperty(BackupConfig.KEYSPACE, keyspace);

    props.setProperty(BackupConfig.DATA_DIR, dataDir);
    props.setProperty(BackupConfig.BACKUP_TYPE, String.valueOf(BackupType.NODE_SNAPSHOT.get()));
    BackupConfig backupConfig = new BackupConfig(props);

    props.setProperty(BaseConfig.DATA_DIR, "/tmp");
    props.setProperty(RestoreConfig.RESTORE_TYPE, String.valueOf(RestoreType.NODE.get()));
    RestoreConfig restoreConfig = new RestoreConfig(props);

    FileUploader uploader =
        new AwsS3FileUploader(
            new SnapshotTraverser(FileSystems.getDefault().getPath(dataDir)),
            TransferManagerBuilder.standard().build(),
            AmazonS3ClientBuilder.defaultClient());
    uploader.upload(backupConfig);

    FileDownloader downloader = new AwsS3FileDownloader(TransferManagerBuilder.standard().build());
    downloader.download(restoreConfig);
  }
}
