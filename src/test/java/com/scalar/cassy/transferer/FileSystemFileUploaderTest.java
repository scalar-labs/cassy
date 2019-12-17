//package com.scalar.cassy.transferer;
//
//import com.amazonaws.services.s3.AmazonS3URI;
//import com.google.common.base.Joiner;
//import com.palantir.giraffe.host.HostAccessors;
//import com.palantir.giraffe.host.HostControlSystem;
//import com.scalar.cassy.config.BackupConfig;
//import com.scalar.cassy.config.BackupType;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//import org.junit.Before;
//import org.junit.Test;
//
//public class FileSystemFileUploaderTest {
//
//  private static final String DATA_DIR = Files.createTempDirectory(null);
//  private static final String KEYSPACE_DIR = "keyspace1";
//  private static final String TABLE_DIR = "standard1-xxx";
//  private static final String SNAPSHOT_DIR = "snapshots";
//  private static final String SNAPSHOT_ID = "1";
//  private static final String FILE1 = "file1";
//  private static final String FILE2 = "file2";
//  private static final String ANY_CLUSTER_ID = "cluster_id";
//  private static final String ANY_SNAPSHOT_ID = "snapshot_id";
//  private static final String ANY_TARGET_IP = "target_ip";
//  private static final String ANY_DATA_DIR = "data_dir";
//  private static final String ANY_S3_URI = "s3://scalar";
//  private static final Joiner joiner = Joiner.on("/").skipNulls();
//  private static final HostControlSystem hcs = HostAccessors.getDefault().open();
//  private AmazonS3URI s3Uri;
//  // @Mock private FileTraverser traverser;
//  private HostControlSystem remoteStorage = HostAccessors.getDefault().open();
//
//  private FileSystemFileUploader uploader;
//
//  private static List<Path> getListOfSnapshotFiles() {
//    return Arrays.asList(
//        hcs.getFileSystem().getPath(
//            DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE1),
//        hcs.getFileSystem().getPath(
//            DATA_DIR, KEYSPACE_DIR, TABLE_DIR, SNAPSHOT_DIR, SNAPSHOT_ID, FILE2));
//  }
//
//  @Before
//  public void setUp() {
//    this.uploader = new FileSystemFileUploader(hcs);
//  }
//
//  public Properties getProperties(BackupType type, String dataDir) {
//    Properties props = new Properties();
//    props.setProperty(BackupConfig.CLUSTER_ID, ANY_CLUSTER_ID);
//    props.setProperty(BackupConfig.SNAPSHOT_ID, ANY_SNAPSHOT_ID);
//    props.setProperty(BackupConfig.BACKUP_TYPE, Integer.toString(type.get()));
//    props.setProperty(BackupConfig.TARGET_IP, ANY_TARGET_IP);
//    props.setProperty(BackupConfig.DATA_DIR, dataDir);
//    props.setProperty(BackupConfig.STORE_BASE_URI, ANY_S3_URI);
//    props.setProperty(BackupConfig.KEYSPACE, KEYSPACE_DIR);
//    return props;
//  }
//
//  @Test
//  public void upload_LocalPathsAndConfigGiven_ShouldUploadProperly() throws InterruptedException {
//    // Arrange
//    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_DATA_DIR));
//    List<Path> paths = getListOfSnapshotFiles();
//    // Act
//    uploader.upload(paths, config);
//
//    // Assert
//
//  }
//
////  @Test
////  public void upload_AmazonServiceExceptionThrown_ShouldThrowFileTransferException()
////      throws InterruptedException {
////    // Arrange
////    BackupConfig config = new BackupConfig(getProperties(BackupType.NODE_SNAPSHOT, ANY_DATA_DIR));
////    AmazonServiceException toThrow = Mockito.mock(AmazonServiceException.class);
////    when(manager.upload(anyString(), anyString(), any(File.class))).thenThrow(toThrow);
////    doReturn(true).when(uploader).requiresUpload(anyString(), anyString(), any(Path.class));
////    List<Path> paths = getListOfSnapshotFiles();
////
////    // Act
////    assertThatThrownBy(() -> uploader.upload(paths, config))
////        .isInstanceOf(FileTransferException.class)
////        .hasCause(toThrow);
////
////    // Assert
////    verify(manager)
////        .upload(
////            s3Uri.getBucket(),
////            BackupPath.create(config, paths.get(0).toString()),
////            Paths.get(config.getDataDir(), paths.get(0).toString()).toFile());
////    verify(upload, never()).waitForCompletion();
////  }
//}
