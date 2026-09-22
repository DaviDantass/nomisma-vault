package com.davidantasdev.nomismavault.marketdata.economic;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fonte de séries econômicas, isolada de controllers e regras de carteira. */
public interface EconomicIndicatorProvider {

  EconomicObservation getLatest(String seriesCode);

  record EconomicObservation(BigDecimal value, LocalDate referenceDate) {}
}
