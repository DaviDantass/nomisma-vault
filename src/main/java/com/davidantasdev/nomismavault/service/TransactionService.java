package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.Transaction;
import com.davidantasdev.nomismavault.entity.enums.TransactionType;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.TransactionMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
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
        private final InvestmentRepository investmentRepository;
        private final TransactionMapper transactionMapper;

        public TransactionService(
                        TransactionRepository transactionRepository,
                        PortfolioRepository portfolioRepository,
                        AssetRepository assetRepository,
                        InvestmentRepository investmentRepository,
                        TransactionMapper transactionMapper) {
                this.transactionRepository = transactionRepository;
                this.portfolioRepository = portfolioRepository;
                this.assetRepository = assetRepository;
                this.investmentRepository = investmentRepository;
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
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Transaction not found for this portfolio"));

                return transactionMapper.toResponse(transaction);
        }

        @Transactional
        public TransactionResponse create(Long portfolioId, TransactionRequest request) {
                Portfolio portfolio = portfolioRepository.findById(portfolioId)
                                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
                Asset asset = assetRepository.findById(request.assetId())
                                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

                Investment investment = investmentRepository.findByPortfolioAndAsset(portfolio, asset)
                                .orElseGet(() -> createNewInvestment(portfolio, asset, request));


                if (request.type() == TransactionType.SELL) {
                        if (!investment.hasEnoughQuantity(request.quantity())) {
                                throw new BusinessException("Insuficient Quantity: "
                                                + investment.getQuantity());
                        }
                }

                investment.updatePosition(request.type(), request.quantity(), request.price());
                if (investment.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        investmentRepository.delete(investment);
                } else {
                        investmentRepository.save(investment);
                }

                Transaction transaction = transactionMapper.toEntity(request);
                transaction.setPortfolio(portfolio);
                transaction.setAsset(asset);
                transaction.setTotalAmount(calculateTotal(request));

                return transactionMapper.toResponse(transactionRepository.save(transaction));
        }

        private Investment createNewInvestment(Portfolio portfolio, Asset asset, TransactionRequest request) {
                if (request.type() == TransactionType.SELL) {
                        throw new BusinessException("You cannot sell property that does not belong to you.");
                }
                Investment investment = new Investment();
                investment.setPortfolio(portfolio);
                investment.setAsset(asset);
                investment.setQuantity(BigDecimal.ZERO);
                investment.setAveragePrice(BigDecimal.ZERO);
                investment.setPurchaseDate(request.transactionDate());
                return investment;
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
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Transaction not found for this portfolio"));

                transactionRepository.delete(transaction);
        }

        public Page<TransactionResponse> getTransactionsByPeriod(
                        Long portfolioId,
                        LocalDate start,
                        LocalDate end,
                        Pageable pageable) {
                Page<Transaction> transactions = transactionRepository
                                .findByPortfolioIdAndTransactionDateBetween(portfolioId, start, end, pageable);

                return transactions.map(transactionMapper::toResponse);
        }

}
