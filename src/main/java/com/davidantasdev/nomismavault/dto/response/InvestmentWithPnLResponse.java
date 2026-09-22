package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;

/**
 * Snapshot de lucro ou prejuízo de um investimento.
 *
 * @param usingFallbackPrice indica que a cotação externa estava indisponível e o cálculo usou a
 *     última cotação salva no ativo ou, em último caso, o preço médio.
 */
public record InvestmentWithPnLResponse(
    Long id,
    String assetTicker,
    BigDecimal quantity,
    BigDecimal averagePrice,
    BigDecimal currentPrice,
    BigDecimal totalInvested,
    BigDecimal marketValue,
    BigDecimal profitLoss,
    BigDecimal profitLossPercent,
    boolean usingFallbackPrice) {}
