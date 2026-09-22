package com.davidantasdev.nomismavault.integration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.davidantasdev.nomismavault.exception.MarketDataNotFoundException;
import com.davidantasdev.nomismavault.exception.MarketDataRateLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class BrapiClientTest {

  private final RestTemplate restTemplate = new RestTemplate();
  private final BrapiClient brapiClient = new BrapiClient(restTemplate);
  private final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

  BrapiClientTest() {
    ReflectionTestUtils.setField(brapiClient, "baseUrl", "https://brapi.test/api");
    ReflectionTestUtils.setField(brapiClient, "apiToken", "");
  }

  @Test
  void mapsProviderRateLimitToInternalException() {
    server
        .expect(once(), requestTo("https://brapi.test/api/quote/PETR4"))
        .andRespond(withStatus(TOO_MANY_REQUESTS));

    assertThrows(MarketDataRateLimitException.class, () -> brapiClient.fetchAssetQuote("PETR4"));
    server.verify();
  }

  @Test
  void mapsMissingTickerToInternalException() {
    server
        .expect(once(), requestTo("https://brapi.test/api/quote/UNKNOWN"))
        .andRespond(withStatus(NOT_FOUND));

    assertThrows(MarketDataNotFoundException.class, () -> brapiClient.fetchAssetQuote("UNKNOWN"));
    server.verify();
  }

  @Test
  void mapsEmptyProviderResultToNotFound() {
    server
        .expect(once(), requestTo("https://brapi.test/api/quote/EMPTY"))
        .andRespond(
            withStatus(org.springframework.http.HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"results\":[]}"));

    assertThrows(MarketDataNotFoundException.class, () -> brapiClient.fetchAssetQuote("EMPTY"));
    server.verify();
  }
}
