package com.scalar.cassy.transferer;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.BaseConfig;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.config.RestoreType;
import com.scalar.cassy.traverser.FileTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

// For a temporary testing. It will be deleted.
public class UploadDownloadTest {
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
  private static final String ANY_TARGET_IP = "target_ip";

  public static void main(String[] args) {
    String dataDir = "/Users/hiroyuki/packages/apache-cassandra-3.11.3/data/data";
    String keyspace = "keyspace1";
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(BackupConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
    props.setProperty(BackupConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(BackupConfig.STORE_BASE_URI, "s3://scalar-tmp");
    props.setProperty(BackupConfig.KEYSPACE, keyspace);

    props.setProperty(BackupConfig.DATA_DIR, dataDir);
    props.setProperty(BackupConfig.BACKUP_TYPE, String.valueOf(BackupType.NODE_SNAPSHOT.get()));
    BackupConfig backupConfig = new BackupConfig(props);

    props.setProperty(BaseConfig.DATA_DIR, "/tmp");
    props.setProperty(RestoreConfig.RESTORE_TYPE, String.valueOf(RestoreType.NODE.get()));
    RestoreConfig restoreConfig = new RestoreConfig(props);

    FileTraverser traverser =
        new SnapshotTraverser(FileSystems.getDefault().getPath(dataDir), ANY_SNAPSHOT_ID);
    List<Path> files = traverser.traverse(keyspace);

    FileUploader uploader =
        new AwsS3FileUploader(
            TransferManagerBuilder.standard().build(), AmazonS3ClientBuilder.defaultClient());
    uploader.upload(files, backupConfig);

    FileDownloader downloader = new AwsS3FileDownloader(TransferManagerBuilder.standard().build());
    downloader.download(restoreConfig);
  }
}
