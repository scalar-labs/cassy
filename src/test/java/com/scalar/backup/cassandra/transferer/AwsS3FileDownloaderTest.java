package com.scalar.backup.cassandra.transferer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.exception.FileTransferException;
import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AwsS3FileDownloaderTest {
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_BACKUP_ID = "backup_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_TMP_DATA_DIR = "tmp_data_dir";
  private static final String ANY_S3_URI = "s3://scalar";
  private AmazonS3URI s3Uri;
  @Mock private TransferManager manager;
  @Mock private MultipleFileDownload download;
  @InjectMocks private AwsS3FileDownloader downloader;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.s3Uri = new AmazonS3URI(ANY_S3_URI);
  }

  public Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(RestoreConfig.BACKUP_ID, ANY_BACKUP_ID);
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(type.get()));
    props.setProperty(RestoreConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(RestoreConfig.DATA_DIR, dataDir);
    props.setProperty(RestoreConfig.DEST_BASE_URI, ANY_S3_URI);
    props.setProperty(RestoreConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  @Test
  public void download_ConfigGiven_ShouldDownloadProperly() throws InterruptedException {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    when(manager.downloadDirectory(anyString(), anyString(), any(File.class))).thenReturn(download);

    // Act
    downloader.download(config);

    // Assert
    verify(manager)
        .downloadDirectory(
            s3Uri.getBucket(),
            BackupPath.create(config, config.getKeyspace()),
            Paths.get(config.getDataDir()).toFile());
    verify(download).waitForCompletion();
  }

  @Test
  public void download_RuntimeExceptionThrown_ShouldThrowFileTransferExceptio()
      throws InterruptedException {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    RuntimeException toThrow = Mockito.mock(RuntimeException.class);
    when(manager.downloadDirectory(anyString(), anyString(), any(File.class))).thenThrow(toThrow);

    // Act
    assertThatThrownBy(() -> downloader.download(config))
        .isInstanceOf(FileTransferException.class)
        .hasCause(toThrow);

    // Assert
    verify(manager)
        .downloadDirectory(
            s3Uri.getBucket(),
            BackupPath.create(config, config.getKeyspace()),
            Paths.get(config.getDataDir()).toFile());
    verify(download, never()).waitForCompletion();
  }
}
