package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.integration.BrapiClient;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private InvestmentService investmentService;

    @Test
    void getInvestmentWithPnL_shouldReturnCorrectPnLUsingMockedPrice() {
        Long portfolioId = 1L;
        Long investmentId = 2L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Asset asset = new Asset();
        asset.setId(3L);
        asset.setTicker("PETR4");
        Investment investment = new Investment();
        investment.setId(investmentId);
        investment.setPortfolio(portfolio);
        investment.setAsset(asset);
        investment.setQuantity(new BigDecimal("10"));
        investment.setAveragePrice(new BigDecimal("100"));
        investment.setPurchaseDate(LocalDate.now());

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByIdAndPortfolio(investmentId, portfolio)).thenReturn(Optional.of(investment));
        when(brapiClient.fetchAssetQuote("PETR4"))
                .thenReturn(new AssetQuoteDTO("PETR4", new BigDecimal("120"), BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now()));

        InvestmentWithPnLResponse response = investmentService.getInvestmentWithPnL(portfolioId, investmentId);

        assertEquals(0, response.currentPrice().compareTo(new BigDecimal("120")));
        assertEquals(0, response.totalInvested().compareTo(new BigDecimal("1000")));
        assertEquals(0, response.marketValue().compareTo(new BigDecimal("1200")));
        assertEquals(0, response.profitLoss().compareTo(new BigDecimal("200")));
        assertEquals(0, response.profitLossPercent().compareTo(new BigDecimal("20.0000")));
    }

    @Test
    void findById_whenInvestmentNotInPortfolio_shouldThrowResourceNotFoundException() {
        Long portfolioId = 1L;
        Long investmentId = 2L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByIdAndPortfolio(investmentId, portfolio)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> investmentService.findById(portfolioId, investmentId));
    }

    private Portfolio portfolioWithOwner(Long portfolioId, Long userId) {
        User user = new User();
        user.setId(userId);
        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioId);
        portfolio.setUser(user);
        return portfolio;
    }
}