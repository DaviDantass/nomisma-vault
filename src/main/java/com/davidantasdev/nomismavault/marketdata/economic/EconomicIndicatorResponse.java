package com.davidantasdev.nomismavault.marketdata.economic;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Última observação pública de um indicador econômico usado para comparação de carteira. */
public record EconomicIndicatorResponse(
    String indicator, String seriesCode, String name, BigDecimal value, LocalDate referenceDate) {}
