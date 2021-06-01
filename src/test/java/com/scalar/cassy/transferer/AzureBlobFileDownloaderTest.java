package com.scalar.cassy.transferer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.FileTransferException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class AzureBlobFileDownloaderTest {
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_TMP_DATA_DIR = "tmp_data_dir";
  private static final String ANY_STOREBASE_URI = "container_name";
  @Mock private BlobContainerClient containerClient;
  @Mock private BlobClient blobClient;
  @Mock private OutputStream outputStream;
  @Mock private PagedIterable<BlobItem> pagedFlux;
  @Spy @InjectMocks private AzureBlobFileDownloader downloader;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void download_TwoFiles_ShouldDownloadBothFiles() throws Exception {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    when(containerClient.listBlobs(any(), any())).thenReturn(pagedFlux);
    File file1 = createTempFile("file_1", "Lorem");
    File file2 = createTempFile("file_2", "Lorem ipsum");
    Assertions.assertThat(file2.delete()).isTrue();
    when(pagedFlux.iterator()).thenReturn(getBlobItems(file1, file2));
    when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
    doReturn(outputStream).when(downloader).writeStream(any(Path.class));
    doNothing().when(blobClient).download(any(OutputStream.class));

    // Act
    downloader.download(config);

    // Assert
    verify(blobClient, times(2)).download(outputStream);
  }

  @Test
  public void download_ExecutionExceptionThrown_ShouldThrowFileTransferException()
      throws Exception {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    ExecutionException toThrow = new ExecutionException(new RuntimeException("foo message"));
    when(containerClient.listBlobs(any(ListBlobsOptions.class), any())).thenReturn(pagedFlux);
    File file1 = createTempFile("file_1", "Lorem");
    File file2 = createTempFile("file_2", "Lorem ipsum");
    when(pagedFlux.iterator()).thenReturn(getBlobItems(file1, file2));
    when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
    doReturn(outputStream).when(downloader).writeStream(any(Path.class));
    doThrow(toThrow.getCause()).when(blobClient).download(any(OutputStream.class));

    // Act
    assertThatThrownBy(() -> downloader.download(config))
        .isInstanceOf(FileTransferException.class)
        .hasCauseInstanceOf(toThrow.getClass());

    // Assert
    verify(blobClient, times(2)).download(outputStream);
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

  private Iterator<BlobItem> getBlobItems(File... files) {
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
        .iterator();
  }
}
