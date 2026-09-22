package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Visão consolidada das carteiras do usuário autenticado. */
public record DashboardResponse(
    int portfoliosCount,
    int activeAlertsCount,
    BigDecimal totalInvested,
    BigDecimal currentValue,
    BigDecimal profitLoss,
    BigDecimal profitLossPercent,
    List<PortfolioSummaryResponse> portfolios) {}
