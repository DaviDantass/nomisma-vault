package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;

public record InvestmentWithPnLResponse(
        Long id,
        String assetTicker,
        BigDecimal quantity,
        BigDecimal averagePrice,
        BigDecimal currentPrice,
        BigDecimal totalInvested,
        BigDecimal marketValue,
        BigDecimal profitLoss,
        BigDecimal profitLossPercent
) {}
