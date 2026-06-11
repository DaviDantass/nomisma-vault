package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import com.davidantasdev.nomismavault.entity.enums.RiskLevel;
import com.davidantasdev.nomismavault.entity.enums.TransactionType;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.mapper.TransactionMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Portfolio portfolio;
    private Asset asset;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);

        InvestmentCategory category = new InvestmentCategory("ACOES", "Acoes", RiskLevel.HIGH);
        asset = new Asset("PETR4", "Petrobras PN", category);
        asset.setId(10L);
    }

    @Test
    void buyCreatesInvestmentPositionAndTransaction() {
        TransactionRequest request = request(TransactionType.BUY, "10", "20.00", "1.50");
        Transaction transaction = new Transaction();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.empty());
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response(TransactionType.BUY, "201.50"));

        transactionService.create(1L, request);

        ArgumentCaptor<Investment> investmentCaptor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentRepository).save(investmentCaptor.capture());

        Investment investment = investmentCaptor.getValue();
        assertEquals(new BigDecimal("10"), investment.getQuantity());
        assertEquals(new BigDecimal("20.00"), investment.getAveragePrice());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(new BigDecimal("201.50"), transactionCaptor.getValue().getTotalAmount());
    }

    @Test
    void sellReducesExistingPosition() {
        TransactionRequest request = request(TransactionType.SELL, "4", "25.00", null);
        Investment investment = new Investment(portfolio, asset, new BigDecimal("10"), new BigDecimal("20.00"),
                LocalDate.now().minusDays(10));

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.of(investment));
        when(transactionMapper.toEntity(request)).thenReturn(new Transaction());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response(TransactionType.SELL, "100.00"));

        transactionService.create(1L, request);

        assertEquals(new BigDecimal("6"), investment.getQuantity());
        assertEquals(new BigDecimal("20.00"), investment.getAveragePrice());
        verify(investmentRepository).save(investment);
    }

    @Test
    void sellMoreThanCurrentPositionFails() {
        TransactionRequest request = request(TransactionType.SELL, "11", "25.00", null);
        Investment investment = new Investment(portfolio, asset, new BigDecimal("10"), new BigDecimal("20.00"),
                LocalDate.now().minusDays(10));

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.of(investment));

        assertThrows(BusinessException.class, () -> transactionService.create(1L, request));

        verify(investmentRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void sellEntirePositionDeletesOpenInvestment() {
        TransactionRequest request = request(TransactionType.SELL, "10", "25.00", null);
        Investment investment = new Investment(portfolio, asset, new BigDecimal("10"), new BigDecimal("20.00"),
                LocalDate.now().minusDays(10));

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.of(investment));
        when(transactionMapper.toEntity(request)).thenReturn(new Transaction());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response(TransactionType.SELL, "250.00"));

        transactionService.create(1L, request);

        assertEquals(BigDecimal.ZERO, investment.getQuantity());
        verify(investmentRepository).delete(investment);
        verify(investmentRepository, never()).save(investment);
    }

    private TransactionRequest request(TransactionType type, String quantity, String price, String fees) {
        return new TransactionRequest(
                1L,
                10L,
                type,
                new BigDecimal(quantity),
                new BigDecimal(price),
                LocalDate.now(),
                fees == null ? null : new BigDecimal(fees),
                null);
    }

    private TransactionResponse response(TransactionType type, String totalAmount) {
        return new TransactionResponse(
                1L,
                1L,
                10L,
                type,
                BigDecimal.ONE,
                BigDecimal.TEN,
                new BigDecimal(totalAmount),
                BigDecimal.ZERO,
                LocalDate.now(),
                null,
                null);
    }
}
