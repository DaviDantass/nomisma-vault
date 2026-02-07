package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.TransactionMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            PortfolioRepository portfolioRepository,
            AssetRepository assetRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.transactionMapper = transactionMapper;
    }

    public Page<TransactionResponse> findAllByPortfolio(Long portfolioId, Pageable pageable) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        return transactionRepository.findAllByPortfolio(portfolio, pageable)
                .map(transactionMapper::toResponse);
    }
    public TransactionResponse findById(Long portfolioId, Long transactionId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Transaction transaction = transactionRepository.findByIdAndPortfolio(transactionId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for this portfolio"));

        return transactionMapper.toResponse(transaction);
    }
    @Transactional
    public TransactionResponse create(Long portfolioId, TransactionRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setPortfolio(portfolio);
        transaction.setAsset(asset);
        transaction.setTotalAmount(calculateTotal(request));

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    private BigDecimal calculateTotal(TransactionRequest request) {
        BigDecimal total = request.quantity().multiply(request.price());
        return (request.fees() != null) ? total.add(request.fees()) : total;
    }
    @Transactional
    public void delete(Long portfolioId, Long transactionId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Transaction transaction = transactionRepository.findByIdAndPortfolio(transactionId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for this portfolio"));

        transactionRepository.delete(transaction);
    }
    public Page<TransactionResponse> getTransactionsByPeriod(
            Long portfolioId,
            LocalDate start,
            LocalDate end,
            Pageable pageable
    ) {
        Page<Transaction> transactions = transactionRepository
                .findByPortfolioIdAndTransactionDateBetween(portfolioId, start, end, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

}
