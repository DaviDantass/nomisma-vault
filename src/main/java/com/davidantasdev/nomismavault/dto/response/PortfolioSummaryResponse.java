package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;

public record PortfolioSummaryResponse(
        Long portfolioId,
        String portfolioName,
        int positionsCount,
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal profitLossPercent
) {
}
