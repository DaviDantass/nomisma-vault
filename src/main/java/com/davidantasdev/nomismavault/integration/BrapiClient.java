package com.davidantasdev.nomismavault.integration;

import com.davidantasdev.nomismavault.dto.integration.BrapiQuote;
import com.davidantasdev.nomismavault.dto.integration.BrapiResponse;
import com.davidantasdev.nomismavault.dto.response.AssetInfoDTO;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.exception.MarketDataNotFoundException;
import com.davidantasdev.nomismavault.exception.MarketDataRateLimitException;
import com.davidantasdev.nomismavault.exception.MarketDataUnavailableException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class BrapiClient {

  private final RestTemplate restTemplate;

  @Value("${brapi.api.url}")
  private String baseUrl;

  @Value("${brapi.api.token:}")
  private String apiToken;

  public BrapiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public AssetQuoteDTO fetchAssetQuote(String ticker) {
    BrapiQuote quote = fetchQuote(ticker);
    return new AssetQuoteDTO(
        quote.symbol(),
        quote.regularMarketPrice(),
        quote.regularMarketChange(),
        quote.regularMarketChangePercent(),
        LocalDateTime.now());
  }

  public AssetInfoDTO fetchAssetInfo(String ticker) {
    BrapiQuote quote = fetchQuote(ticker);
    return new AssetInfoDTO(quote.symbol(), quote.longName(), quote.regularMarketPrice());
  }

  private BrapiQuote fetchQuote(String ticker) {
    String url = baseUrl + "/quote/" + ticker;
    try {
      HttpHeaders headers = new HttpHeaders();
      if (apiToken != null && !apiToken.isBlank()) {
        headers.setBearerAuth(apiToken);
      }

      ResponseEntity<BrapiResponse> responseEntity =
          restTemplate.exchange(
              url, HttpMethod.GET, new HttpEntity<>(headers), BrapiResponse.class);
      BrapiResponse response = responseEntity.getBody();

      BrapiQuote quote =
          Optional.ofNullable(response)
              .map(BrapiResponse::results)
              .filter(list -> !list.isEmpty())
              .map(list -> list.get(0))
              .orElseThrow(
                  () -> new MarketDataNotFoundException("Cotação não encontrada para " + ticker));
      log.debug("Market data provider returned quote for {}", ticker);
      return quote;

    } catch (HttpClientErrorException.TooManyRequests e) {
      log.warn("Market data provider rate limited ticker {}", ticker);
      throw new MarketDataRateLimitException("Limite de consultas do provedor atingido", e);
    } catch (HttpClientErrorException.NotFound e) {
      throw new MarketDataNotFoundException("Cotação não encontrada para " + ticker, e);
    } catch (ResourceAccessException e) {
      log.warn("Market data provider unavailable for {}: {}", ticker, e.getMessage());
      throw new MarketDataUnavailableException("Provedor de cotações indisponível", e);
    } catch (RestClientException e) {
      log.warn("Market data provider failed for {}: {}", ticker, e.getMessage());
      throw new MarketDataUnavailableException("Falha ao consultar o provedor de cotações", e);
    }
  }
}
