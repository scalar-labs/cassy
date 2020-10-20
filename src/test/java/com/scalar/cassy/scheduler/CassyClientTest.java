package com.scalar.cassy.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.OperationStatus;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CassyClientTest {
  private static final String MOCKED_CLUSTER_ID = "cluster123";
  @Mock CassyGrpc.CassyBlockingStub blockingStub;
  private CassyClient client;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.client = spy(new CassyClient(blockingStub));
  }

  @Test
  public void takeClusterSnapshot_ShouldBeSuccessful() throws Exception {
    // arrange
    BackupRequest request = prepareBackupRequest(BackupType.CLUSTER_SNAPSHOT.get());
    BackupResponse response = prepareBackupResponse(BackupType.CLUSTER_SNAPSHOT.get());
    BackupListingRequest listingRequest = prepareListingRequest(response);
    BackupListingResponse listingResponse = prepareListingResponse(3);
    when(blockingStub.takeBackup(request)).thenReturn(response);
    when(blockingStub.listBackups(listingRequest)).thenReturn(listingResponse);

    // act
    int result = client.takeClusterSnapshot(MOCKED_CLUSTER_ID, 1);

    // assert
    assertThat(result).isEqualTo(0);
  }

  @Test
  public void takeNodeSnapshot_ShouldBeSuccessful() throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupRequest request2 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(response1);
    BackupListingRequest listingRequest2 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse(3);
    BackupListingResponse listingResponse2 = prepareListingResponse(3);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);

    // act
    int result = client.takeNodeSnapshot(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(0);
  }

  @Test
  public void takeNodeSnapshot_WithOneFailingIp_ShouldReturnStatusCode1() throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupRequest request2 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(response1);
    BackupListingRequest listingRequest2 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse(3);
    BackupListingResponse listingResponse2 = prepareListingResponse(4);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);

    // act
    int result = client.takeNodeSnapshot(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void takeIncrementalSnapshot_WithOneFailingIp_ShouldReturnStatusCode1() throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupRequest request2 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(targetIps.get(0));
    BackupListingRequest listingRequest2 = prepareListingRequest(targetIps.get(1));
    BackupListingRequest listingRequest3 = prepareListingRequest(response1);
    BackupListingRequest listingRequest4 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse();
    BackupListingResponse listingResponse2 = prepareListingResponse();
    BackupListingResponse listingResponse3 = prepareListingResponse(3);
    BackupListingResponse listingResponse4 = prepareListingResponse(4);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);
    when(blockingStub.listBackups(listingRequest3)).thenReturn(listingResponse3);
    when(blockingStub.listBackups(listingRequest4)).thenReturn(listingResponse4);

    // act
    int result = client.takeIncrementalBackup(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void takeIncrementalSnapshot_ShouldBeSuccessful() throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupRequest request2 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(targetIps.get(0));
    BackupListingRequest listingRequest2 = prepareListingRequest(targetIps.get(1));
    BackupListingRequest listingRequest3 = prepareListingRequest(response1);
    BackupListingRequest listingRequest4 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse();
    BackupListingResponse listingResponse2 = prepareListingResponse();
    BackupListingResponse listingResponse3 = prepareListingResponse(3);
    BackupListingResponse listingResponse4 = prepareListingResponse(3);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);
    when(blockingStub.listBackups(listingRequest3)).thenReturn(listingResponse3);
    when(blockingStub.listBackups(listingRequest4)).thenReturn(listingResponse4);

    // act
    int result = client.takeIncrementalBackup(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(0);
  }

  @Test
  public void takeClusterSnapshot_TimeoutExceptionOccurs_ShouldReturnStatusCode1()
      throws Exception {
    // arrange
    BackupRequest request = prepareBackupRequest(BackupType.CLUSTER_SNAPSHOT.get());
    BackupResponse response = prepareBackupResponse(BackupType.CLUSTER_SNAPSHOT.get());
    BackupListingRequest listingRequest = prepareListingRequest(response);
    BackupListingResponse listingResponse = prepareListingResponse(2);
    when(blockingStub.takeBackup(request)).thenReturn(response);
    when(blockingStub.listBackups(listingRequest)).thenReturn(listingResponse);

    // act
    int result = client.takeClusterSnapshot(MOCKED_CLUSTER_ID, 1);

    // assert
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void takeNodeSnapshot_TimeoutExceptionOccurs_ShouldReturnStatusCode1() throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupRequest request2 = prepareBackupRequest(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_SNAPSHOT.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(response1);
    BackupListingRequest listingRequest2 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse(3);
    BackupListingResponse listingResponse2 = prepareListingResponse(1);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);

    // act
    int result = client.takeNodeSnapshot(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void takeIncrementalSnapshot_TimeoutExceptionOccurs_ShouldReturnStatusCode1()
      throws Exception {
    // arrange
    List<String> targetIps = Arrays.asList("127.0.0.1", "127.0.0.2");
    BackupRequest request1 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupRequest request2 =
        prepareBackupRequest(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupResponse response1 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(0));
    BackupResponse response2 =
        prepareBackupResponse(BackupType.NODE_INCREMENTAL.get(), targetIps.get(1));
    BackupListingRequest listingRequest1 = prepareListingRequest(targetIps.get(0));
    BackupListingRequest listingRequest2 = prepareListingRequest(targetIps.get(1));
    BackupListingRequest listingRequest3 = prepareListingRequest(response1);
    BackupListingRequest listingRequest4 = prepareListingRequest(response2);
    BackupListingResponse listingResponse1 = prepareListingResponse();
    BackupListingResponse listingResponse2 = prepareListingResponse();
    BackupListingResponse listingResponse3 = prepareListingResponse(3);
    BackupListingResponse listingResponse4 = prepareListingResponse(1);
    when(blockingStub.takeBackup(request1)).thenReturn(response1);
    when(blockingStub.takeBackup(request2)).thenReturn(response2);
    when(blockingStub.listBackups(listingRequest1)).thenReturn(listingResponse1);
    when(blockingStub.listBackups(listingRequest2)).thenReturn(listingResponse2);
    when(blockingStub.listBackups(listingRequest3)).thenReturn(listingResponse3);
    when(blockingStub.listBackups(listingRequest4)).thenReturn(listingResponse4);

    // act
    int result = client.takeIncrementalBackup(MOCKED_CLUSTER_ID, 1, targetIps);

    // assert
    assertThat(result).isEqualTo(1);
  }

  private BackupListingResponse prepareListingResponse() {
    return BackupListingResponse.newBuilder()
        .addEntries(
            BackupListingResponse.Entry.newBuilder()
                .setBackupType(BackupType.NODE_INCREMENTAL.get()))
        .addEntries(
            BackupListingResponse.Entry.newBuilder()
                .setBackupType(BackupType.NODE_SNAPSHOT.get())
                .setStatusValue(OperationStatus.COMPLETED_VALUE)
                .build())
        .build();
  }

  private BackupListingRequest prepareListingRequest(String targetIp) {
    return BackupListingRequest.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setTargetIp(targetIp)
        .build();
  }

  private BackupListingResponse prepareListingResponse(int status) {
    return BackupListingResponse.newBuilder()
        .addEntries(
            BackupListingResponse.Entry.newBuilder()
                .setStatusValue(status)
                .setStatus(OperationStatus.forNumber(status))
                .build())
        .build();
  }

  private BackupListingRequest prepareListingRequest(BackupResponse response) {
    return BackupListingRequest.newBuilder()
        .setClusterId(response.getClusterId())
        .setSnapshotId(response.getSnapshotId())
        .setTargetIp(response.getTargetIps(0))
        .setLimit(1)
        .build();
  }

  private BackupRequest prepareBackupRequest(int backupType) {
    return BackupRequest.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setBackupType(backupType)
        .build();
  }

  private BackupRequest prepareBackupRequest(int backupType, String targetIps) {
    return BackupRequest.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setBackupType(backupType)
        .addTargetIps(targetIps)
        .build();
  }

  private BackupResponse prepareBackupResponse(int type, String targetIp) {
    return BackupResponse.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setSnapshotId("snapshot123")
        .addTargetIps(targetIp)
        .setBackupType(type)
        .build();
  }

  private BackupResponse prepareBackupResponse(int type) {
    return BackupResponse.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setSnapshotId("snapshot123")
        .addTargetIps("127.0.0.1")
        .setBackupType(type)
        .build();
  }
}
