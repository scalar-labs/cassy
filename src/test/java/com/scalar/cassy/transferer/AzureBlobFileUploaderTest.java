package com.scalar.cassy.transferer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.google.common.base.Joiner;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.exception.FileTransferException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class AzureBlobFileUploaderTest {
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
  private static final String ANY_STOREBASE_URI = "container_name";
  private static final Joiner joiner = Joiner.on("/").skipNulls();
  private static final FileSystem fs = FileSystems.getDefault();
  @Mock private BlobContainerClient containerClient;
  @Mock private BlobClient blobClient;
  @Mock private InputStream inputStream;
  @Spy @InjectMocks private AzureBlobFileUploader uploader;

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
    props.setProperty(BackupConfig.STORE_BASE_URI, ANY_STOREBASE_URI);
    props.setProperty(BackupConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  @Test
  public void upload_LocalPathsAndConfigGiven_ShouldUploadProperly() {
    // Arrange
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, DATA_DIR));
    doReturn(true).when(uploader).requiresUpload(anyString(), any(Path.class));
    when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
    doReturn(inputStream).when(uploader).readStream(any(Path.class));
    doNothing().when(blobClient).upload(any(InputStream.class), anyLong(),anyBoolean());
    List<Path> paths = getListOfSnapshotFiles();

    // Act
    uploader.upload(paths, config);

    // Assert
    Path dataDir = Paths.get(DATA_DIR);
    verify(containerClient)
        .getBlobClient(BackupPath.create(config, dataDir.relativize(paths.get(0)).toString()));
    verify(containerClient)
        .getBlobClient(BackupPath.create(config, dataDir.relativize(paths.get(1)).toString()));
    verify(blobClient, times(2)).upload(inputStream, 0L, true);
  }

  @Test
  public void upload_ExecutionExceptionThrown_ShouldThrowFileTransferException()  {
    // Arrange
    List<Path> paths = getListOfSnapshotFiles();
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, DATA_DIR));
    ExecutionException toThrow = new ExecutionException(new RuntimeException("foo message"));
    doReturn(true).when(uploader).requiresUpload(anyString(), any(Path.class));
    when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
    doReturn(inputStream).when(uploader).readStream(any(Path.class));
    doThrow(toThrow.getCause()).when(blobClient).upload(any(InputStream.class), anyLong(), anyBoolean());

    // Act
    assertThatThrownBy(() -> uploader.upload(paths, config))
        .isInstanceOf(FileTransferException.class)
        .hasCauseInstanceOf(toThrow.getClass());

    // Assert
    Path dataDir = Paths.get(DATA_DIR);
    verify(containerClient)
        .getBlobClient(BackupPath.create(config, dataDir.relativize(paths.get(0)).toString()));
    verify(containerClient)
        .getBlobClient(BackupPath.create(config, dataDir.relativize(paths.get(1)).toString()));
    verify(blobClient, times(2)).upload(inputStream, 0L, true);
  }
}
