package com.davidantasdev.nomismavault.marketdata.economic;

import com.davidantasdev.nomismavault.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Adapter HTTP do SGS/BCB; somente esta classe conhece o formato da API do Banco Central. */
@Component
public class BcbEconomicIndicatorProvider implements EconomicIndicatorProvider {

  private static final DateTimeFormatter BCB_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public BcbEconomicIndicatorProvider(
      RestTemplate restTemplate,
      @Value("${bcb.api.url:https://api.bcb.gov.br/dados/serie/bcdata.sgs}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  @Override
  public EconomicObservation getLatest(String seriesCode) {
    try {
      BcbEntry[] entries =
          restTemplate.getForObject(
              baseUrl + "." + seriesCode + "/dados/ultimos/1?formato=json", BcbEntry[].class);
      return Arrays.stream(entries == null ? new BcbEntry[0] : entries)
          .findFirst()
          .map(
              entry ->
                  new EconomicObservation(
                      new BigDecimal(entry.value().replace(',', '.')),
                      LocalDate.parse(entry.date(), BCB_DATE)))
          .orElseThrow(() -> new BusinessException("Indicador econômico sem observações"));
    } catch (BusinessException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new BusinessException("Indicador econômico indisponível", exception);
    }
  }

  private record BcbEntry(String data, String valor) {
    String date() {
      return data;
    }

    String value() {
      return valor;
    }
  }
}
