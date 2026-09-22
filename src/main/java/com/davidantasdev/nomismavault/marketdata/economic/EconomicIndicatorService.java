package com.davidantasdev.nomismavault.marketdata.economic;

import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Traduz indicadores suportados em séries do Banco Central sem expor códigos de infraestrutura. */
@Service
@RequiredArgsConstructor
public class EconomicIndicatorService {

  private static final Map<String, IndicatorDefinition> INDICATORS =
      Map.of(
          "selic", new IndicatorDefinition("11", "SELIC diária"),
          "ipca", new IndicatorDefinition("433", "IPCA mensal"));

  private final EconomicIndicatorProvider provider;

  public EconomicIndicatorResponse getLatest(String indicator) {
    String normalized = indicator.toLowerCase(Locale.ROOT);
    IndicatorDefinition definition = INDICATORS.get(normalized);
    if (definition == null) {
      throw new ResourceNotFoundException("Indicador não suportado: " + indicator);
    }
    var observation = provider.getLatest(definition.seriesCode());
    return new EconomicIndicatorResponse(
        normalized,
        definition.seriesCode(),
        definition.name(),
        observation.value(),
        observation.referenceDate());
  }

  private record IndicatorDefinition(String seriesCode, String name) {}
}
