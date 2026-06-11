package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.enums.RiskLevel;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.integration.BrapiClient;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private InvestmentMapper investmentMapper;

    @Mock
    private BrapiClient brapiClient;

    @InjectMocks
    private InvestmentService investmentService;

    @Test
    void getInvestmentWithPnLReturnsCalculatedPositionForPortfolioInvestment() {
        Long portfolioId = 1L;
        Long investmentId = 100L;
        Portfolio portfolio = new Portfolio();

        InvestmentCategory category = new InvestmentCategory("ACOES", "Acoes", RiskLevel.HIGH);
        Asset asset = new Asset("AAPL", "Apple Inc.", category);

        Investment investment = mock(Investment.class);
        when(investment.getId()).thenReturn(investmentId);
        when(investment.getAsset()).thenReturn(asset);
        when(investment.getQuantity()).thenReturn(new BigDecimal("10"));
        when(investment.getAveragePrice()).thenReturn(new BigDecimal("150"));
        when(investment.calculateTotalInvested()).thenReturn(new BigDecimal("1500"));
        when(investment.calculateMarketValue(any())).thenReturn(new BigDecimal("1600"));
        when(investment.calculateProfitLoss(any())).thenReturn(new BigDecimal("100"));
        when(investment.calculateProfitLossPercent(any())).thenReturn(new BigDecimal("6.67"));

        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByIdAndPortfolio(investmentId, portfolio)).thenReturn(Optional.of(investment));

        AssetQuoteDTO quote = mock(AssetQuoteDTO.class);
        when(quote.price()).thenReturn(new BigDecimal("160"));
        when(brapiClient.fetchAssetQuote("AAPL")).thenReturn(quote);

        InvestmentWithPnLResponse response = investmentService.getInvestmentWithPnL(portfolioId, investmentId);

        assertNotNull(response);
        assertEquals(investmentId, response.id());
        assertEquals("AAPL", response.assetTicker());
        assertEquals(new BigDecimal("10"), response.quantity());
        assertEquals(new BigDecimal("150"), response.averagePrice());
        assertEquals(new BigDecimal("160"), response.currentPrice());
        assertEquals(new BigDecimal("1500"), response.totalInvested());
        assertEquals(new BigDecimal("1600"), response.marketValue());
        assertEquals(new BigDecimal("100"), response.profitLoss());
        assertEquals(new BigDecimal("6.67"), response.profitLossPercent());

        verify(portfolioRepository).findById(portfolioId);
        verify(investmentRepository).findByIdAndPortfolio(investmentId, portfolio);
        verify(brapiClient).fetchAssetQuote("AAPL");
    }

    @Test
    void getInvestmentWithPnLRejectsInvestmentFromAnotherPortfolio() {
        Long portfolioId = 1L;
        Long investmentId = 999L;
        Portfolio portfolio = new Portfolio();

        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByIdAndPortfolio(investmentId, portfolio)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> investmentService.getInvestmentWithPnL(portfolioId, investmentId));

        assertEquals("Investment not found for this portfolio", exception.getMessage());
        verify(investmentRepository).findByIdAndPortfolio(investmentId, portfolio);
        verifyNoInteractions(brapiClient);
    }
}
