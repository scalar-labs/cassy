package com.scalar.cassy.transferer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.core.http.rest.PagedFlux;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.config.RestoreConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

public class AzureFileDownloaderTest {
  private static final String KEYSPACE_DIR = "keyspace1";
  private static final String ANY_CLUSTER_ID = "cluster_id";
  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
  private static final String ANY_TARGET_IP = "target_ip";
  private static final String ANY_TMP_DATA_DIR = "tmp_data_dir";
  private static final String ANY_S3_URI = "https://scalar.blob.core.windows.net/container";
  private static final String MOCKED_BLOB_NAME = "blob_name";
  @Mock private BlobContainerAsyncClient containerClient;
  @Mock private BlobAsyncClient blobClient;
  private AzureFileDownloader downloader;
  @Mock private PagedFlux<BlobItem> pagedFlux;
  @Mock private Mono<BlobProperties> blobPropertiesMono;
  @Mock private CompletableFuture<BlobProperties> future;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    downloader = new AzureFileDownloader(this.containerClient);
  }

  public Properties getProperties(BackupType type, String dataDir) {
    Properties props = new Properties();
    props.setProperty(RestoreConfig.CLUSTER_ID, ANY_CLUSTER_ID);
    props.setProperty(RestoreConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
    props.setProperty(RestoreConfig.RESTORE_TYPE, Integer.toString(type.get()));
    props.setProperty(RestoreConfig.TARGET_IP, ANY_TARGET_IP);
    props.setProperty(RestoreConfig.DATA_DIR, dataDir);
    props.setProperty(RestoreConfig.STORE_BASE_URI, ANY_S3_URI);
    props.setProperty(RestoreConfig.KEYSPACE, KEYSPACE_DIR);
    return props;
  }

  private Iterable<BlobItem> getMockedList() {
    BlobItem blobItem = new BlobItem().setName(MOCKED_BLOB_NAME);
    List<BlobItem> list = new ArrayList<>();
    list.add(blobItem);
    return list;
  }

  @Test
  public void download_ConfigGiven_ShouldDownloadProperly() throws Exception {
    // Arrange
    RestoreConfig config =
        new RestoreConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_TMP_DATA_DIR));
    when(containerClient.listBlobs(any(ListBlobsOptions.class))).thenReturn(pagedFlux);
    when(pagedFlux.toIterable()).thenReturn(getMockedList());
    when(containerClient.getBlobAsyncClient(anyString())).thenReturn(blobClient);
    when(blobClient.downloadToFile(anyString())).thenReturn(blobPropertiesMono);
    when(blobPropertiesMono.toFuture()).thenReturn(future);
    when(future.get()).thenReturn(mock(BlobProperties.class));

    // Act
    downloader.download(config);

    // Assert
    verify(future).get();
  }
}
