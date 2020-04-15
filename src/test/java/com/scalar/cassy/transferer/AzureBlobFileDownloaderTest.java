package com.scalar.cassy.transferer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.core.http.rest.PagedFlux;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

public class AzureBlobFileDownloaderTest {
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_TMP_DATA_DIR = "tmp_data_dir";
  private static final String ANY_STOREBASE_URI = "container_name";
  @Mock private BlobContainerAsyncClient containerClient;
  @Mock private BlobAsyncClient blobClient;
  private AzureBlobFileDownloader downloader;
  @Mock private PagedFlux<BlobItem> pagedFlux;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    downloader = new AzureBlobFileDownloader(this.containerClient);
  }

  @Test
  public void download_TwoFiles_ShouldDownloadBothFiles() throws Exception {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    when(containerClient.listBlobs(any())).thenReturn(pagedFlux);
    File file1 = createTempFile("file_1", "Lorem");
    File file2 = createTempFile("file_2", "Lorem ipsum");
    Assertions.assertThat(file2.delete()).isTrue();
    when(pagedFlux.toIterable()).thenReturn(getBlobItems(file1, file2));
    when(containerClient.getBlobAsyncClient(file1.getName())).thenReturn(blobClient);
    when(blobClient.downloadToFile(anyString())).thenReturn(Mono.just(mock(BlobProperties.class)));
    when(containerClient.getBlobAsyncClient(file2.getName())).thenReturn(blobClient);
    when(blobClient.downloadToFile(anyString())).thenReturn(Mono.just(mock(BlobProperties.class)));
    // Act
    downloader.download(config);

    // Assert
    verify(blobClient).downloadToFile(Paths.get(config.getDataDir(), file1.getName()).toString());
    verify(blobClient).downloadToFile(Paths.get(config.getDataDir(), file2.getName()).toString());
  }

  @Test
  public void download_IOExceptionThrown_ShouldThrowFileTransferException() throws Exception {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    IOException toThrow = new IOException("foo message");
    when(containerClient.listBlobs(any(ListBlobsOptions.class))).thenReturn(pagedFlux);
    File file1 = createTempFile("file_1", "Lorem");
    File file2 = createTempFile("file_2", "Lorem ipsum");
    when(pagedFlux.toIterable()).thenReturn(getBlobItems(file1, file2));
    when(containerClient.getBlobAsyncClient(anyString())).thenReturn(blobClient);
    Mono<BlobProperties> blobPropertiesMono1 = Mono.just(mock(BlobProperties.class));
    Mono<BlobProperties> blobPropertiesMono2 = Mono.error(toThrow);
    when(blobClient.downloadToFile(anyString()))
        .thenReturn(blobPropertiesMono1)
        .thenReturn(blobPropertiesMono2);

    // Act
    assertThatThrownBy(() -> downloader.download(config))
        .isInstanceOf(FileTransferException.class)
        .hasCauseInstanceOf(IOException.class);

    // Assert
    verify(blobClient).downloadToFile(Paths.get(config.getDataDir(), file1.getName()).toString());
    verify(blobClient).downloadToFile(Paths.get(config.getDataDir(), file2.getName()).toString());
  }

  private Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(RestoreConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(type.get()));
    props.setProperty(RestoreConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(RestoreConfig.DATA_DIR, dataDir);
    props.setProperty(RestoreConfig.STORE_BASE_URI, ANY_STOREBASE_URI);
    props.setProperty(RestoreConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  private File createTempFile(String name, String content) throws IOException {
    File file = new File(Files.createTempFile(name, ".txt").toString());
    file.deleteOnExit();
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(content);
    fileWriter.close();
    return file;
  }

  private List<BlobItem> getBlobItems(File... files) {
    return Arrays.stream(files)
        .map(
            f -> {
              BlobItem blob = new BlobItem();
              blob.setName(f.getName());
              BlobItemProperties properties = new BlobItemProperties();
              properties.setContentLength(f.length());
              blob.setProperties(properties);
              return blob;
            })
        .collect(Collectors.toList());
  }
}
