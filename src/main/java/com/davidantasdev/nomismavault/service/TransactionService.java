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
import com.davidantasdev.nomismavault.repository.TransactionRepository;
import com.davidantasdev.nomismavault.security.PortfolioAccessService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final AssetRepository assetRepository;
  private final InvestmentRepository investmentRepository;
  private final TransactionMapper transactionMapper;
  private final PortfolioAccessService portfolioAccessService;
  private final FinancialAuditService financialAuditService;

  public TransactionService(
      TransactionRepository transactionRepository,
      AssetRepository assetRepository,
      InvestmentRepository investmentRepository,
      TransactionMapper transactionMapper,
      PortfolioAccessService portfolioAccessService,
      FinancialAuditService financialAuditService) {
    this.transactionRepository = transactionRepository;
    this.assetRepository = assetRepository;
    this.investmentRepository = investmentRepository;
    this.transactionMapper = transactionMapper;
    this.portfolioAccessService = portfolioAccessService;
    this.financialAuditService = financialAuditService;
  }

  public Page<TransactionResponse> findAllByPortfolio(Long portfolioId, Pageable pageable) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    return transactionRepository
        .findAllByPortfolio(portfolio, pageable)
        .map(transactionMapper::toResponse);
  }

  public TransactionResponse findById(Long portfolioId, Long transactionId) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    Transaction transaction =
        transactionRepository
            .findByIdAndPortfolio(transactionId, portfolio)
            .orElseThrow(
                () -> new ResourceNotFoundException("Transaction not found for this portfolio"));

    return transactionMapper.toResponse(transaction);
  }

  @Transactional
  public TransactionResponse create(Long portfolioId, TransactionRequest request) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);
    Asset asset =
        assetRepository
            .findById(request.assetId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

    Investment investment =
        investmentRepository
            .findByPortfolioAndAssetForUpdate(portfolio, asset)
            .orElseGet(() -> createNewInvestment(portfolio, asset, request));

    if (request.type() == TransactionType.SELL) {
      if (!investment.hasEnoughQuantity(request.quantity())) {
        throw new BusinessException("Insuficient Quantity: " + investment.getQuantity());
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

    Transaction savedTransaction = transactionRepository.save(transaction);
    financialAuditService.record("TRANSACTION_CREATED", portfolio, savedTransaction);
    return transactionMapper.toResponse(savedTransaction);
  }

  private Investment createNewInvestment(
      Portfolio portfolio, Asset asset, TransactionRequest request) {
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
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    Transaction transaction =
        transactionRepository
            .findByIdAndPortfolio(transactionId, portfolio)
            .orElseThrow(
                () -> new ResourceNotFoundException("Transaction not found for this portfolio"));

    transactionRepository.delete(transaction);
    financialAuditService.record("TRANSACTION_DELETED", portfolio, transaction);
    recalculateInvestment(portfolio, transaction.getAsset());
  }

  private void recalculateInvestment(Portfolio portfolio, Asset asset) {
    var transactions =
        transactionRepository.findAllByPortfolioAndAssetOrderByTransactionDateAscIdAsc(
            portfolio, asset);
    Investment investment =
        investmentRepository
            .findByPortfolioAndAsset(portfolio, asset)
            .orElseGet(() -> new Investment());

    BigDecimal quantity = BigDecimal.ZERO;
    BigDecimal averagePrice = BigDecimal.ZERO;
    LocalDate purchaseDate = null;

    for (Transaction historicalTransaction : transactions) {
      if (historicalTransaction.getType() == TransactionType.BUY) {
        BigDecimal totalCost =
            quantity
                .multiply(averagePrice)
                .add(
                    historicalTransaction.getQuantity().multiply(historicalTransaction.getPrice()));
        quantity = quantity.add(historicalTransaction.getQuantity());
        averagePrice = totalCost.divide(quantity, 2, java.math.RoundingMode.HALF_UP);
        if (purchaseDate == null) {
          purchaseDate = historicalTransaction.getTransactionDate();
        }
      } else {
        if (quantity.compareTo(historicalTransaction.getQuantity()) < 0) {
          throw new BusinessException(
              "Cannot delete transaction because it would invalidate transaction history");
        }
        quantity = quantity.subtract(historicalTransaction.getQuantity());
      }
    }

    if (quantity.compareTo(BigDecimal.ZERO) == 0) {
      investmentRepository
          .findByPortfolioAndAsset(portfolio, asset)
          .ifPresent(investmentRepository::delete);
      return;
    }

    investment.setPortfolio(portfolio);
    investment.setAsset(asset);
    investment.setQuantity(quantity);
    investment.setAveragePrice(averagePrice);
    investment.setPurchaseDate(purchaseDate);
    investmentRepository.save(investment);
  }

  public Page<TransactionResponse> getTransactionsByPeriod(
      Long portfolioId, LocalDate start, LocalDate end, Pageable pageable) {
    portfolioAccessService.getOwnedPortfolio(portfolioId);
    Page<Transaction> transactions =
        transactionRepository.findByPortfolioIdAndTransactionDateBetween(
            portfolioId, start, end, pageable);

    return transactions.map(transactionMapper::toResponse);
  }
}
