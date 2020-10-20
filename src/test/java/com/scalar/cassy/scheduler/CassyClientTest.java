package com.scalar.cassy.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.scalar.cassy.config.BackupType;
import com.scalar.cassy.rpc.BackupListingRequest;
import com.scalar.cassy.rpc.BackupListingResponse;
import com.scalar.cassy.rpc.BackupRequest;
import com.scalar.cassy.rpc.BackupResponse;
import com.scalar.cassy.rpc.CassyGrpc;
import com.scalar.cassy.rpc.OperationStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CassyClientTest {
  @Mock CassyGrpc.CassyBlockingStub blockingStub;
  private CassyClient client;
  private static final String MOCKED_CLUSTER_ID = "cluster123";

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.client = spy(new CassyClient(blockingStub));
  }

  @Test
  public void takeClusterSnapshot_TimeoutExceptionOccurs_ShouldReturnStatusCode1() throws Exception {
    // arrange
    BackupRequest request = prepareBackupRequest();
    BackupResponse response = prepareBackupResponse(1);
    BackupListingRequest listingRequest = prepareListingRequest(response);
    BackupListingResponse listingResponse = prepareListingResponse(2);
    when(blockingStub.takeBackup(request)).thenReturn(response);
    when(blockingStub.listBackups(listingRequest)).thenReturn(listingResponse);

    // act
    int result = client.takeClusterSnapshot(MOCKED_CLUSTER_ID, 3);

    // assert
    assertThat(result).isEqualTo(1);
  }

  private BackupListingResponse prepareListingResponse(int status) {
    return BackupListingResponse.newBuilder()
        .addEntries(
            BackupListingResponse.Entry.newBuilder()
            .setStatusValue(status)
            .setStatus(OperationStatus.forNumber(status))
            .build()
        ).build();
  }

  private BackupListingRequest prepareListingRequest(BackupResponse response) {
    return BackupListingRequest.newBuilder()
        .setClusterId(response.getClusterId())
        .setSnapshotId(response.getSnapshotId())
        .setTargetIp(response.getTargetIps(0))
        .setLimit(1)
        .build();
  }

  private BackupRequest prepareBackupRequest() {
    return BackupRequest.newBuilder()
        .setClusterId(MOCKED_CLUSTER_ID)
        .setBackupType(BackupType.CLUSTER_SNAPSHOT.get())
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
