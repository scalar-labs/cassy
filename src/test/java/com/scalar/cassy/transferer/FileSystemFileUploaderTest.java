package com.scalar.cassy.transferer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Joiner;
import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FileSystemFileUploaderTest {
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
  private static final Joiner joiner = Joiner.on("/").skipNulls();
  private static final FileSystem fs = FileSystems.getDefault();
  private static final URI REMOTE_FILE_SYSTEM_URI =
      URI.create("ssh://bar@192.168.2.106/home/bar/file_sys");
  private RemoteFileSystemConnection connectionManager;

  private static List<Path> getListOfSnapshotFiles() {
    return Arrays.asList(
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE1)),
        fs.getPath(
            joiner.join(DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE2)));
  }

  public Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(BackupConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
    props.setProperty(BackupConfig.BACKUP_TYPE, Integer.toString(type.get()));
    props.setProperty(BackupConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(BackupConfig.DATA_DIR, dataDir);
    props.setProperty(BackupConfig.STORE_BASE_URI, REMOTE_FILE_SYSTEM_URI.toString());
    props.setProperty(BackupConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  @Before
  public void setUpConnectionManager() {
    HostControlSystem hostControlSystem = Mockito.mock(HostControlSystem.class);
    connectionManager = mock(RemoteFileSystemConnection.class);
    when(connectionManager.getHostControlSystem()).thenReturn(hostControlSystem);
    when(connectionManager.getStoragePath()).thenReturn(REMOTE_FILE_SYSTEM_URI.getPath());
  }

  @Test
  public void uploadSingleFile_WhichAlreadyExist_ShouldNotUploadFile() throws IOException {
    // arrange
    Path toBackup = Files.createTempFile(null, null);
    new File(toBackup.toString()).deleteOnExit();

    String key = toBackup.getFileName().toString();
    String storeBaseConfig = "ssh://bar@127.0.0.1" + toBackup.getParent().toString();
    RemoteFileSystemFileUploader uploader =
        spy(new RemoteFileSystemFileUploader(connectionManager));
    when(connectionManager
            .getHostControlSystem()
            .getPath(URI.create(storeBaseConfig).getPath(), key))
        .thenReturn(toBackup);

    // act
    uploader.upload(toBackup, key, storeBaseConfig);

    // assert
    verify(uploader, never()).createDirectories(any());
    verify(uploader, never()).copyFile(any(), any());
  }

  @Test
  public void uploadSingleFile_WhichDoesNotExist_ShouldUploadFile() throws IOException {
    // arrange
    Path toBackup = Files.createTempFile(null, null);
    new File(toBackup.toString()).deleteOnExit();
    String key = "copy_" + toBackup.getFileName().toString();
    String storeBaseConfig = "ssh://bar@127.0.0.1" + toBackup.getParent().toString();
    RemoteFileSystemFileUploader uploader =
        spy(new RemoteFileSystemFileUploader(connectionManager));
    when(connectionManager
            .getHostControlSystem()
            .getPath(URI.create(storeBaseConfig).getPath(), key))
        .thenReturn(toBackup);
    Path destFile = Paths.get(toBackup.getParent().toString(), key);
    new File(destFile.toString()).deleteOnExit();
    doNothing().when(uploader).createDirectories(destFile);
    doNothing().when(uploader).copyFile(toBackup, destFile);

    // act
    uploader.upload(toBackup, key, storeBaseConfig);
  }

  @Test
  public void uploadListOfFiles_CorrectParameters_ShouldBeSuccessful() throws IOException {
    // arrange
    List<Path> paths = getListOfSnapshotFiles();
    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, DATA_DIR));
    RemoteFileSystemFileUploader uploader =
        spy(new RemoteFileSystemFileUploader(connectionManager));
    String remoteFileSystemBaseDir = URI.create(config.getStoreBaseUri()).getPath();
    String relativeFilePath1 = Paths.get(DATA_DIR).relativize(paths.get(0)).toString();
    String relativeFilePath2 = Paths.get(DATA_DIR).relativize(paths.get(1)).toString();
    Path targetFile1Path = Paths.get(remoteFileSystemBaseDir, relativeFilePath1);
    Path targetFile2Path = Paths.get(remoteFileSystemBaseDir, relativeFilePath2);
    when(connectionManager
            .getHostControlSystem()
            .getPath(
                REMOTE_FILE_SYSTEM_URI.getPath(), BackupPath.create(config, relativeFilePath1)))
        .thenReturn(targetFile1Path);
    when(connectionManager
            .getHostControlSystem()
            .getPath(
                REMOTE_FILE_SYSTEM_URI.getPath(), BackupPath.create(config, relativeFilePath2)))
        .thenReturn(targetFile2Path);
    doNothing().when(uploader).copyFile(paths.get(0), targetFile1Path);
    doNothing().when(uploader).copyFile(paths.get(1), targetFile2Path);
    doNothing().when(uploader).createDirectories(targetFile1Path.getParent());
    doNothing().when(uploader).createDirectories(targetFile2Path.getParent());

    // act
    uploader.upload(paths, config);
  }
}
