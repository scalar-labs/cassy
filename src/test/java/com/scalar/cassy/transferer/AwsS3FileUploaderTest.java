package com.scalar.cassy.transferer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.base.Joiner;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.FileTransferException;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class AwsS3FileUploaderTest {
  private static final String DATA_DIR = "/tmp/" + UUID.randomUUID();
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String TABLE_DIR = "standard1-xxx";
  private static final String SNAPSHOT_DIR = "snapshots";
  private static final String SNAPSHOT_ID = "1";
  private static final String FILE1 = "file1";
  private static final String FILE2 = "file2";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_S3_URI = "s3://scalar";
  private static final Joiner joiner = Joiner.on("/").skipNulls();
  private static final FileSystem fs = FileSystems.getDefault();
  @Mock private TransferManager manager;
  @Mock private AmazonS3 s3;
  @Mock private AmazonS3URI s3Uri;
  @Mock private Upload upload;
  @Spy @InjectMocks private AwsS3FileUploader uploader;

  private static List<Path> getListOfSnapshotFiles() {
    return Arrays.asList(
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE1)),
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE2)));
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  public Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(BackupConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
    props.setProperty(BackupConfig.BACKUP_TYPE, Integer.toString(type.get()));
    props.setProperty(BackupConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(BackupConfig.DATA_DIR, dataDir);
    props.setProperty(BackupConfig.STORE_BASE_URI, ANY_S3_URI);
    props.setProperty(BackupConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  @Test
  public void upload_LocalPathsAndConfigGiven_ShouldUploadProperly() throws InterruptedException {
    // Arrange
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, DATA_DIR));
    when(upload.getDescription()).thenReturn("anything");
    when(upload.getState()).thenReturn(Transfer.TransferState.Completed);
    when(manager.upload(anyString(), anyString(), any(File.class))).thenReturn(upload);
    when(s3Uri.getBucket()).thenReturn(ANY_S3_URI);
    doReturn(true).when(uploader).requiresUpload(anyString(), anyString(), any(Path.class));
    List<Path> paths = getListOfSnapshotFiles();

    // Act
    uploader.upload(paths, config);

    // Assert
    Path dataDir = Paths.get(DATA_DIR);
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            BackupPath.create(config, dataDir.relativize(paths.get(0)).toString()),
            paths.get(0).toFile());
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            BackupPath.create(config, dataDir.relativize(paths.get(1)).toString()),
            paths.get(1).toFile());
    verify(upload, times(2)).waitForCompletion();
  }

  @Test
  public void upload_AmazonServiceExceptionThrown_ShouldThrowFileTransferException()
      throws InterruptedException {
    // Arrange
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, DATA_DIR));
    AmazonServiceException toThrow = Mockito.mock(AmazonServiceException.class);
    when(manager.upload(anyString(), anyString(), any(File.class))).thenThrow(toThrow);
    when(s3Uri.getBucket()).thenReturn(ANY_S3_URI);
    doReturn(true).when(uploader).requiresUpload(anyString(), anyString(), any(Path.class));
    List<Path> paths = getListOfSnapshotFiles();

    // Act
    assertThatThrownBy(() -> uploader.upload(paths, config))
        .isInstanceOf(FileTransferException.class)
        .hasCause(toThrow);

    // Assert
    Path dataDir = Paths.get(DATA_DIR);
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            BackupPath.create(config, dataDir.relativize(paths.get(0)).toString()),
            paths.get(0).toFile());
    verify(upload, never()).waitForCompletion();
  }
}
