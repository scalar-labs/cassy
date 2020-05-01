package com.scalar.cassy.transferer;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.palantir.giraffe.host.HostControlSystem;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.config.RestoreType;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RemoteFileSystemFileDownloaderTest {
  private static final String KEYSPACE = "keyspace1";
  private static final URI remoteFileSystemURI =
      URI.create("ssh://bar@192.168.2.106/home/bar/file_sys");
  private RemoteFileSystemConnection connectionManager;

  public Properties getProperties() {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, UUID.randomUUID().toString());
    props.setProperty(RestoreConfig.SNAPSHOT_ID, UUID.randomUUID().toString());
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(RestoreType.NODE.get()));
    props.setProperty(RestoreConfig.TARGET_IP, "target_ip");
    props.setProperty(RestoreConfig.DATA_DIR, "tmp_data_dir");
    props.setProperty(RestoreConfig.STORE_BASE_URI, remoteFileSystemURI.toString());
    props.setProperty(RestoreConfig.KEYSPACE, KEYSPACE);
    return props;
  }

  @Before
  public void setUpConnectionManager() {
    HostControlSystem hostControlSystem = Mockito.mock(HostControlSystem.class);
    connectionManager = mock(RemoteFileSystemConnection.class);
    when(connectionManager.getHostControlSystem()).thenReturn(hostControlSystem);
    when(connectionManager.getStoragePath()).thenReturn(remoteFileSystemURI.getPath());
  }

  @Test
  public void download_CorrectParameters_ShouldBeSuccessful() throws IOException {
    // arrange
    RestoreConfig config = new RestoreConfig(getProperties());
    Path backupKeyspacePath =
        Paths.get(remoteFileSystemURI.getPath(), BackupPath.create(config, config.getKeyspace()));

    when(connectionManager
            .getHostControlSystem()
            .getPath(
                remoteFileSystemURI.getPath(), BackupPath.create(config, config.getKeyspace())))
        .thenReturn(backupKeyspacePath);
    RemoteFileSystemFileDownloader downloader =
        spy(new RemoteFileSystemFileDownloader(connectionManager));
    Path destDir = Paths.get(config.getDataDir(), BackupPath.create(config, config.getKeyspace()));
    doNothing().when(downloader).createDirectories(destDir.getParent());
    doNothing().when(downloader).copyFolder(backupKeyspacePath, destDir);

    // act
    downloader.download(config);
  }
}
