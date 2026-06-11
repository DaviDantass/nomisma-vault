package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.entity.enums.RiskLevel;
import com.davidantasdev.nomismavault.mapper.PortfolioMapper;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private PortfolioMapper portfolioMapper;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void getSummaryCalculatesPortfolioTotalsFromOpenPositions() {
        User user = new User();
        user.setId(1L);

        Portfolio portfolio = new Portfolio(user, "Main", "Long term");
        portfolio.setId(10L);

        InvestmentCategory category = new InvestmentCategory("ACOES", "Acoes", RiskLevel.HIGH);

        Asset asset = new Asset("PETR4", "Petrobras PN", category);
        asset.setCurrentPrice(new BigDecimal("25.00"));

        Asset assetWithoutCurrentPrice = new Asset("CDB", "CDB Banco", category);

        Investment first = new Investment(portfolio, asset, new BigDecimal("10"), new BigDecimal("20.00"),
                LocalDate.now());
        Investment second = new Investment(portfolio, assetWithoutCurrentPrice, new BigDecimal("2"),
                new BigDecimal("100.00"), LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(portfolioRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findAllByPortfolio(portfolio)).thenReturn(List.of(first, second));

        PortfolioSummaryResponse summary = portfolioService.getSummary(1L, 10L);

        assertEquals(10L, summary.portfolioId());
        assertEquals("Main", summary.portfolioName());
        assertEquals(2, summary.positionsCount());
        assertEquals(new BigDecimal("400.00"), summary.totalInvested());
        assertEquals(new BigDecimal("450.00"), summary.currentValue());
        assertEquals(new BigDecimal("50.00"), summary.profitLoss());
        assertEquals(new BigDecimal("12.5000"), summary.profitLossPercent());
    }
}
