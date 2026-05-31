package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.entity.enums.TransactionType;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.mapper.TransactionMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.TransactionRepository;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void create_whenBuy_shouldCreateNewInvestmentAndTransaction() {
        Long portfolioId = 1L;
        Long assetId = 2L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Asset asset = asset(assetId);
        TransactionRequest request = new TransactionRequest(
                portfolioId,
                assetId,
                TransactionType.BUY,
                new BigDecimal("5"),
                new BigDecimal("10"),
                LocalDate.now(),
                BigDecimal.ZERO,
                "buy");
        Transaction transaction = new Transaction();
        TransactionResponse expected = new TransactionResponse(
                99L, portfolioId, assetId, TransactionType.BUY, request.quantity(), request.price(),
                new BigDecimal("50"), BigDecimal.ZERO, request.transactionDate(), request.notes(), null);

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.empty());
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(expected);

        TransactionResponse actual = transactionService.create(portfolioId, request);

        ArgumentCaptor<Investment> investmentCaptor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentRepository).save(investmentCaptor.capture());
        Investment savedInvestment = investmentCaptor.getValue();
        assertEquals(new BigDecimal("5"), savedInvestment.getQuantity());
        assertEquals(new BigDecimal("10.00"), savedInvestment.getAveragePrice());
        assertEquals(expected, actual);
    }

    @Test
    void create_whenSellWithInsufficientQuantity_shouldThrowBusinessException() {
        Long portfolioId = 1L;
        Long assetId = 2L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Asset asset = asset(assetId);
        Investment investment = new Investment();
        investment.setQuantity(new BigDecimal("1"));
        investment.setAveragePrice(new BigDecimal("10"));
        TransactionRequest request = new TransactionRequest(
                portfolioId,
                assetId,
                TransactionType.SELL,
                new BigDecimal("2"),
                new BigDecimal("10"),
                LocalDate.now(),
                BigDecimal.ZERO,
                "sell");

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.of(investment));

        assertThrows(BusinessException.class, () -> transactionService.create(portfolioId, request));
    }

    @Test
    void create_whenSellWithNoExistingInvestment_shouldThrowBusinessException() {
        Long portfolioId = 1L;
        Long assetId = 2L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Asset asset = asset(assetId);
        TransactionRequest request = new TransactionRequest(
                portfolioId,
                assetId,
                TransactionType.SELL,
                new BigDecimal("1"),
                new BigDecimal("10"),
                LocalDate.now(),
                BigDecimal.ZERO,
                "sell");

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> transactionService.create(portfolioId, request));
    }

    @Test
    void delete_whenBuyTransaction_shouldRevertInvestmentQuantity() {
        Long portfolioId = 1L;
        Long transactionId = 3L;
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Asset asset = asset(2L);
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAsset(asset);
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(new BigDecimal("3"));

        Investment investment = new Investment();
        investment.setQuantity(new BigDecimal("10"));
        investment.setAveragePrice(new BigDecimal("20"));

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(transactionRepository.findByIdAndPortfolio(transactionId, portfolio)).thenReturn(Optional.of(transaction));
        when(investmentRepository.findByPortfolioAndAsset(portfolio, asset)).thenReturn(Optional.of(investment));

        transactionService.delete(portfolioId, transactionId);

        ArgumentCaptor<Investment> investmentCaptor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentRepository).save(investmentCaptor.capture());
        assertEquals(new BigDecimal("7"), investmentCaptor.getValue().getQuantity());
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void getTransactionsByPeriod_shouldCallRepositoryWithCorrectParams() {
        Long portfolioId = 1L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);
        Portfolio portfolio = portfolioWithOwner(portfolioId, 10L);
        Transaction transaction = new Transaction();
        TransactionResponse response = new TransactionResponse(
                1L, portfolioId, 2L, TransactionType.BUY, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ZERO, LocalDate.now(), null, null);

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(10L);
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(transactionRepository.findByPortfolioIdAndTransactionDateBetween(portfolioId, start, end, pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        Page<TransactionResponse> result = transactionService.getTransactionsByPeriod(portfolioId, start, end, pageable);

        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByPortfolioIdAndTransactionDateBetween(eq(portfolioId), eq(start), eq(end), eq(pageable));
    }

    private Portfolio portfolioWithOwner(Long portfolioId, Long userId) {
        User user = new User();
        user.setId(userId);
        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioId);
        portfolio.setUser(user);
        return portfolio;
    }

    private Asset asset(Long id) {
        Asset asset = new Asset();
        asset.setId(id);
        return asset;
    }
}
