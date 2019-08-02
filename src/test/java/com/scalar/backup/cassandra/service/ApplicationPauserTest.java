package com.scalar.backup.cassandra.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scalar.backup.cassandra.exception.PauseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;

public class ApplicationPauserTest {
  private final String SRV_SERVICE_URL = "srv_service_url.";
  private final String APP_IP1 = "ip1";
  private final String APP_IP2 = "ip2";
  private final String APP_IP3 = "ip3";
  private final int PORT = 50051;
  private ApplicationPauser pauser;

  @Before
  public void setUp() {
    pauser = spy(new ApplicationPauser(SRV_SERVICE_URL));
  }

  public List<SRVRecord> prepareSrvRecords() throws TextParseException {
    return Arrays.asList(
        new SRVRecord(
            new Name(SRV_SERVICE_URL), DClass.IN, 60, 0, 0, PORT, new Name(APP_IP1 + ".")),
        new SRVRecord(
            new Name(SRV_SERVICE_URL), DClass.IN, 60, 0, 0, PORT, new Name(APP_IP2 + ".")),
        new SRVRecord(
            new Name(SRV_SERVICE_URL), DClass.IN, 60, 0, 0, PORT, new Name(APP_IP3 + ".")));
  }

  @Test
  public void pause_ApplicationIpsGiven_ShouldPauseAll() throws TextParseException {
    // Arrange
    List<SRVRecord> records = prepareSrvRecords();
    doReturn(records).when(pauser).getApplicationIps(SRV_SERVICE_URL);
    ApplicationPauseClient client1 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP1, PORT)).thenReturn(client1);
    ApplicationPauseClient client2 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP2, PORT)).thenReturn(client2);
    ApplicationPauseClient client3 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP3, PORT)).thenReturn(client3);

    // Act
    pauser.pause();

    // Assert
    verify(pauser).getClient(APP_IP1, PORT);
    verify(client1).pause();
    verify(pauser).getClient(APP_IP2, PORT);
    verify(client2).pause();
    verify(pauser).getClient(APP_IP3, PORT);
    verify(client3).pause();
  }

  @Test
  public void pause_SomeExceptionThrown_ShouldThrowPauseException() throws TextParseException {
    // Arrange
    List<SRVRecord> records = prepareSrvRecords();
    doReturn(records).when(pauser).getApplicationIps(SRV_SERVICE_URL);
    ApplicationPauseClient client1 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP1, PORT)).thenReturn(client1);
    RuntimeException toThrow = mock(RuntimeException.class);
    doThrow(toThrow).when(client1).pause();
    ApplicationPauseClient client2 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP2, PORT)).thenReturn(client2);
    ApplicationPauseClient client3 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP3, PORT)).thenReturn(client3);

    // Act Assert
    assertThatThrownBy(() -> pauser.pause())
        .isInstanceOf(PauseException.class)
        .hasCauseExactlyInstanceOf(ExecutionException.class);
  }

  @Test
  public void unpause_ApplicationIpsGiven_ShouldUnpauseAll() throws TextParseException {
    // Arrange
    List<SRVRecord> records = prepareSrvRecords();
    doReturn(records).when(pauser).getApplicationIps(SRV_SERVICE_URL);
    ApplicationPauseClient client1 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP1, PORT)).thenReturn(client1);
    ApplicationPauseClient client2 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP2, PORT)).thenReturn(client2);
    ApplicationPauseClient client3 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP3, PORT)).thenReturn(client3);

    // Act
    pauser.unpause();

    // Assert
    verify(pauser).getClient(APP_IP1, PORT);
    verify(client1).unpause();
    verify(pauser).getClient(APP_IP2, PORT);
    verify(client2).unpause();
    verify(pauser).getClient(APP_IP3, PORT);
    verify(client3).unpause();
  }

  @Test
  public void unpause_SomeExceptionThrown_ShouldThrowPauseException() throws TextParseException {
    // Arrange
    List<SRVRecord> records = prepareSrvRecords();
    doReturn(records).when(pauser).getApplicationIps(SRV_SERVICE_URL);
    ApplicationPauseClient client1 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP1, PORT)).thenReturn(client1);
    RuntimeException toThrow = mock(RuntimeException.class);
    doThrow(toThrow).when(client1).unpause();
    ApplicationPauseClient client2 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP2, PORT)).thenReturn(client2);
    ApplicationPauseClient client3 = mock(ApplicationPauseClient.class);
    when(pauser.getClient(APP_IP3, PORT)).thenReturn(client3);

    // Act Assert
    assertThatThrownBy(() -> pauser.unpause())
        .isInstanceOf(PauseException.class)
        .hasCauseExactlyInstanceOf(ExecutionException.class);
  }
}
