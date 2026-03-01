package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.integration.BrapiClient;
import com.davidantasdev.nomismavault.dto.response.*;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.entity.enums.RiskLevel;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private BrapiClient brapiClient;

    @InjectMocks
    private InvestmentService investmentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInvestmentWithPnL_Success() {
        Long portfolioId = 1L;
        Long investmentId = 100L;

        InvestmentCategory category = new InvestmentCategory(
                "Ações",
                "Investimentos em ações de empresas",
                RiskLevel.HIGH);

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

        when(investmentRepository.findById(investmentId)).thenReturn(Optional.of(investment));

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

        verify(investmentRepository).findById(investmentId);
        verify(brapiClient).fetchAssetQuote("AAPL");
    }

    @Test
    void testGetInvestmentWithPnL_NotFound() {
        Long investmentId = 999L;
        when(investmentRepository.findById(investmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            investmentService.getInvestmentWithPnL(1L, investmentId);
        });

        assertEquals("Investment não encontrado", exception.getMessage());
        verify(investmentRepository).findById(investmentId);
        verifyNoInteractions(brapiClient);
    }
}