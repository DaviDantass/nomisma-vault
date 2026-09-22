package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.exception.MarketDataNotFoundException;
import com.davidantasdev.nomismavault.exception.MarketDataRateLimitException;
import com.davidantasdev.nomismavault.exception.MarketDataUnavailableException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.marketdata.MarketDataService;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.security.PortfolioAccessService;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestmentService {
  private final InvestmentRepository investmentRepository;
  private final InvestmentMapper investmentMapper;
  private final MarketDataService marketDataService;
  private final PortfolioAccessService portfolioAccessService;

  public InvestmentService(
      InvestmentRepository investmentRepository,
      InvestmentMapper investmentMapper,
      MarketDataService marketDataService,
      PortfolioAccessService portfolioAccessService) {
    this.investmentRepository = investmentRepository;
    this.investmentMapper = investmentMapper;
    this.marketDataService = marketDataService;
    this.portfolioAccessService = portfolioAccessService;
  }

  /**
   * Calcula o P&amp;L usando a cotação atual quando disponível. Se o provedor de mercado falhar, a
   * posição continua consultável com a última cotação persistida no ativo; sem ela, o preço médio
   * evita interromper a consulta.
   */
  @Transactional(readOnly = true)
  public InvestmentWithPnLResponse getInvestmentWithPnL(Long portfolioId, Long investmentId) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    Investment investment =
        investmentRepository
            .findByIdAndPortfolio(investmentId, portfolio)
            .orElseThrow(
                () -> new ResourceNotFoundException("Investment not found for this portfolio"));

    Asset asset = investment.getAsset();
    BigDecimal currentPrice;
    boolean usingFallbackPrice = false;
    try {
      AssetQuoteDTO quote = marketDataService.getQuote(asset.getTicker());
      currentPrice = quote.price();
    } catch (MarketDataUnavailableException
        | MarketDataRateLimitException
        | MarketDataNotFoundException exception) {
      currentPrice =
          asset.getCurrentPrice() != null ? asset.getCurrentPrice() : investment.getAveragePrice();
      usingFallbackPrice = true;
    }

    return new InvestmentWithPnLResponse(
        investment.getId(),
        asset.getTicker(),
        investment.getQuantity(),
        investment.getAveragePrice(),
        currentPrice,
        investment.calculateTotalInvested(),
        investment.calculateMarketValue(currentPrice),
        investment.calculateProfitLoss(currentPrice),
        investment.calculateProfitLossPercent(currentPrice),
        usingFallbackPrice);
  }

  @Transactional(readOnly = true)
  public Page<InvestmentResponse> findAllByPortfolio(Long portfolioId, Pageable pageable) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    return investmentRepository
        .findAllByPortfolio(portfolio, pageable)
        .map(investmentMapper::toResponse);
  }

  @Transactional(readOnly = true)
  public InvestmentResponse findById(Long portfolioId, Long investmentId) {
    Portfolio portfolio = portfolioAccessService.getOwnedPortfolio(portfolioId);

    Investment investment =
        investmentRepository
            .findByIdAndPortfolio(investmentId, portfolio)
            .orElseThrow(
                () -> new ResourceNotFoundException("Investment not found for this portfolio"));

    return investmentMapper.toResponse(investment);
  }
}
