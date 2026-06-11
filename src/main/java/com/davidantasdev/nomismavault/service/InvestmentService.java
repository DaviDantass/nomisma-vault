package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.integration.BrapiClient;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final InvestmentMapper investmentMapper;
    private final BrapiClient brapiClient;

    public InvestmentService(
            InvestmentRepository investmentRepository,
            PortfolioRepository portfolioRepository,
            AssetRepository assetRepository,
            InvestmentMapper investmentMapper,
            BrapiClient brapiClient) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.investmentMapper = investmentMapper;
        this.brapiClient = brapiClient;
    }

    public InvestmentWithPnLResponse getInvestmentWithPnL(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found for this portfolio"));

        Asset asset = investment.getAsset();
        AssetQuoteDTO quote = brapiClient.fetchAssetQuote(asset.getTicker());
        BigDecimal currentPrice = quote.price();

        return new InvestmentWithPnLResponse(
                investment.getId(),
                asset.getTicker(),
                investment.getQuantity(),
                investment.getAveragePrice(),
                currentPrice,
                investment.calculateTotalInvested(),
                investment.calculateMarketValue(currentPrice),
                investment.calculateProfitLoss(currentPrice),
                investment.calculateProfitLossPercent(currentPrice));
    }

    public Page<InvestmentResponse> findAllByPortfolio(Long portfolioId, Pageable pageable) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        return investmentRepository.findAllByPortfolio(portfolio, pageable)
                .map(investmentMapper::toResponse);
    }

    public InvestmentResponse findById(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found for this portfolio"));

        return investmentMapper.toResponse(investment);
    }

    @Transactional
    public InvestmentResponse create(Long portfolioId, Long assetId, InvestmentRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Investment investment = investmentMapper.toEntity(request);
        investment.setPortfolio(portfolio);
        investment.setAsset(asset);
        Investment saved = investmentRepository.save(investment);
        return investmentMapper.toResponse(saved);
    }

    @Transactional
    public InvestmentResponse update(Long portfolioId, Long investmentId, Long assetId, InvestmentRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found for this portfolio"));

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Investment investmentData = investmentMapper.toEntity(request);

        investment.setAsset(asset);
        investment.setQuantity(investmentData.getQuantity());
        investment.setAveragePrice(investmentData.getAveragePrice());
        investment.setPurchaseDate(investmentData.getPurchaseDate());
        investment.setNotes(investmentData.getNotes());

        return investmentMapper.toResponse(investment);
    }

    @Transactional
    public void delete(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found for this portfolio"));

        investmentRepository.delete(investment);
    }
}
