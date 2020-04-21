package com.scalar.cassy.transferer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.BackupConfig;
import com.scalar.cassy.config.BackupType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import org.junit.Test;
import org.mockito.Mockito;

public class FileSystemFileUploaderTest {
  private static final String KEYSPACE = "keyspace1";
  private static final URI remoteFileSystemURI =
      URI.create("ssh://bar@192.168.2.106/home/bar/file_sys");

  public Properties getProperties() {
    Properties props = new Properties();
    props.setProperty(BackupConfig.CLUSTER_ID, UUID.randomUUID().toString());
    props.setProperty(BackupConfig.SNAPSHOT_ID, UUID.randomUUID().toString());
    props.setProperty(BackupConfig.BACKUP_TYPE, Integer.toString(BackupType.NODE_SNAPSHOT.get()));
    props.setProperty(BackupConfig.TARGET_IP, "target_ip");
    props.setProperty(BackupConfig.DATA_DIR, "tmp_data_dir");
    props.setProperty(BackupConfig.STORE_BASE_URI, remoteFileSystemURI.toString());
    props.setProperty(BackupConfig.KEYSPACE, KEYSPACE);
    return props;
  }

  @Test
  public void uploadSingleFile_WhichAlreadyExist_ShouldNotUploadFile() throws IOException {
    // arrange
    Path toBackup = Files.createTempFile(null, null);
    new File(toBackup.toString()).deleteOnExit();

    String key = toBackup.getFileName().toString();
    String storeBaseConfig = "ssh://bar@127.0.0.1" + toBackup.getParent().toString();
    HostControlSystem hostConnection = mock(HostControlSystem.class);
    FileSystemFileUploader uploader = spy(new FileSystemFileUploader(hostConnection));
    when(hostConnection.getPath(URI.create(storeBaseConfig).getPath(), key)).thenReturn(toBackup);

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
    HostControlSystem hostConnection = mock(HostControlSystem.class);
    FileSystemFileUploader uploader = spy(new FileSystemFileUploader(hostConnection));
    when(hostConnection.getPath(URI.create(storeBaseConfig).getPath(), key)).thenReturn(toBackup);
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
    BackupConfig config = new BackupConfig(getProperties());
    HostControlSystem hostConnection = mock(HostControlSystem.class);
    FileSystemFileUploader uploader = spy(new FileSystemFileUploader(hostConnection));
    Path remoteStoragePath =
        Paths.get(remoteFileSystemURI.getPath(), BackupPath.create(config, KEYSPACE));
    when(hostConnection.getPath(remoteFileSystemURI.getPath(), BackupPath.create(config, KEYSPACE)))
        .thenReturn(remoteStoragePath);
    Mockito.doNothing().when(uploader).createDirectories(remoteStoragePath.getParent());
    Path sourceDir = Paths.get(config.getDataDir(), config.getKeyspace());
    String host =
        String.format("%s@%s", remoteFileSystemURI.getUserInfo(), remoteFileSystemURI.getHost());
    doNothing().when(uploader).copyFolder(sourceDir, remoteStoragePath);

    // act
    uploader.upload(new ArrayList<>(), config);
  }
}
