package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceHistoryResponse(
        Long id,
        Long assetId,
        String assetTicker,
        String assetName,
        BigDecimal price,
        LocalDate date) {
}
