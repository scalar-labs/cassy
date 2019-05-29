package com.scalar.backup.cassandra.syncer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.base.Joiner;
import com.scalar.backup.cassandra.config.BackupConfig;
import com.scalar.backup.cassandra.config.BackupType;
import com.scalar.backup.cassandra.config.ConfigBase;
import com.scalar.backup.cassandra.config.RestoreConfig;
import com.scalar.backup.cassandra.exception.FileTransferException;
import com.scalar.backup.cassandra.traverser.FileTraverser;
import com.scalar.backup.cassandra.traverser.SnapshotTraverser;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AwsS3FileSyncerTest {
  private static final String DATA_DIR = "/tmp/" + UUID.randomUUID();
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String TABLE_DIR = "standard1-xxx";
  private static final String SNAPSHOT_DIR = "snapshots";
  private static final String BACKUP_DIR = "backups";
  private static final String SNAPSHOT_ID = "1";
  private static final String FILE1 = "file1";
  private static final String FILE2 = "file2";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_BACKUP_ID = "backup_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_DATA_DIR = "data_dir";
  private static final String ANY_TMP_DATA_DIR = "tmp_data_dir";
  private static final String ANY_S3_URI = "s3://scalar";
  private static final Joiner joiner = Joiner.on("/").skipNulls();
  private static final FileSystem fs = FileSystems.getDefault();
  private AwsS3FileSyncer syncer;
  private AmazonS3URI s3Uri;
  private URI uri;
  @Mock private TransferManager manager;
  @Mock private AmazonS3 s3;
  @Mock private Upload upload;
  @Mock private MultipleFileDownload download;

  private static List<Path> getListOfSnapshotFiles() {
    return Arrays.asList(
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE1)),
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE2)));
  }

  private static List<Path> getListOfBackupFiles() {
    return Arrays.asList(
        fs.getPath(joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, BACKUP_DIR, FILE1)),
        fs.getPath(joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, BACKUP_DIR, FILE2)));
  }

  @Before
  public void setUp() throws URISyntaxException {
    MockitoAnnotations.initMocks(this);
    this.s3Uri = new AmazonS3URI(ANY_S3_URI);
    this.uri = new URI(ANY_S3_URI);
  }

  public Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(ConfigBase.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(ConfigBase.BACKUP_ID, ANY_BACKUP_ID);
    props.setProperty(ConfigBase.BACKUP_TYPE, Integer.toString(type.get()));
    props.setProperty(ConfigBase.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(ConfigBase.DATA_DIR, dataDir);
    props.setProperty(ConfigBase.DEST_BASE_URI, ANY_S3_URI);
    props.setProperty(ConfigBase.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  @Test
  public void upload_LocalPathsAndConfigGiven_ShouldUploadProperly() throws InterruptedException {
    // Arrange
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_DATA_DIR));
    syncer = spy(new AwsS3FileSyncer(manager, s3));
    when(manager.upload(anyString(), anyString(), any(File.class))).thenReturn(upload);
    doReturn(true).when(syncer).isUpdated(anyString(), anyString(), any(Path.class));
    List<Path> paths = getListOfSnapshotFiles();

    // Act
    syncer.upload(paths, config);

    // Assert
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            syncer.getKey(paths.get(0).toString(), config),
            Paths.get(config.getDataDir(), paths.get(0).toString()).toFile());
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            syncer.getKey(paths.get(1).toString(), config),
            Paths.get(config.getDataDir(), paths.get(1).toString()).toFile());
    verify(upload, times(2)).waitForCompletion();
  }

  @Test
  public void upload_AmazonServiceExceptionThrown_ShouldThrowFileTransferException()
      throws InterruptedException {
    // Arrange
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_DATA_DIR));
    syncer = spy(new AwsS3FileSyncer(manager, s3));
    AmazonServiceException toThrow = Mockito.mock(AmazonServiceException.class);
    when(manager.upload(anyString(), anyString(), any(File.class))).thenThrow(toThrow);
    doReturn(true).when(syncer).isUpdated(anyString(), anyString(), any(Path.class));
    List<Path> paths = getListOfSnapshotFiles();

    // Act
    assertThatThrownBy(() -> syncer.upload(paths, config))
        .isInstanceOf(FileTransferException.class)
        .hasCause(toThrow);

    // Assert
    verify(manager)
        .upload(
            s3Uri.getBucket(),
            syncer.getKey(paths.get(0).toString(), config),
            Paths.get(config.getDataDir(), paths.get(0).toString()).toFile());
    verify(upload, never()).waitForCompletion();
  }

  @Test
  public void download_ConfigGiven_ShouldDownloadProperly() throws InterruptedException {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    syncer = spy(new AwsS3FileSyncer(manager, s3));
    when(manager.downloadDirectory(anyString(), anyString(), any(File.class))).thenReturn(download);

    // Act
    syncer.download(config);

    // Assert
    verify(manager)
        .downloadDirectory(
            s3Uri.getBucket(),
            syncer.getKey(config.getKeyspace(), config),
            Paths.get(config.getDataDir()).toFile());
    verify(download).waitForCompletion();
  }

  @Test
  public void download_RuntimeExceptionThrown_ShouldThrowFileTransferExceptio()
      throws InterruptedException {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    syncer = spy(new AwsS3FileSyncer(manager, s3));
    RuntimeException toThrow = Mockito.mock(RuntimeException.class);
    when(manager.downloadDirectory(anyString(), anyString(), any(File.class))).thenThrow(toThrow);

    // Act
    assertThatThrownBy(() -> syncer.download(config))
        .isInstanceOf(FileTransferException.class)
        .hasCause(toThrow);

    // Assert
    verify(manager)
        .downloadDirectory(
            s3Uri.getBucket(),
            syncer.getKey(config.getKeyspace(), config),
            Paths.get(config.getDataDir()).toFile());
    verify(download, never()).waitForCompletion();
  }

  // It will be deleted.
  @Ignore
  @Test
  public void tmp() {
    String dataDir = "/Users/hiroyuki/packages/apache-cassandra-3.11.3/data/data";
    String keyspace = "keyspace1";
    Properties props = new Properties();
    props.setProperty(ConfigBase.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(ConfigBase.BACKUP_ID, ANY_BACKUP_ID);
    props.setProperty(ConfigBase.BACKUP_TYPE, String.valueOf(BackupType.NODE_SNAPSHOT.get()));
    props.setProperty(ConfigBase.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(ConfigBase.DEST_BASE_URI, "s3://scalar-tmp");
    props.setProperty(ConfigBase.KEYSPACE, keyspace);

    props.setProperty(ConfigBase.DATA_DIR, dataDir);
    BackupConfig backupConfig = new BackupConfig(props);
    props.setProperty(ConfigBase.DATA_DIR, "/tmp");
    RestoreConfig restoreConfig = new RestoreConfig(props);
    FileTraverser traverser = new SnapshotTraverser(FileSystems.getDefault().getPath(dataDir));
    List<Path> paths = traverser.traverse(keyspace);
    FileSyncer syncer =
        new AwsS3FileSyncer(
            TransferManagerBuilder.standard().build(), AmazonS3ClientBuilder.defaultClient());

    syncer.upload(paths, backupConfig);
    syncer.download(restoreConfig);
  }
}
