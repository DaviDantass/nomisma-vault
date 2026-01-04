package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;

    public InvestmentService(
            InvestmentRepository investmentRepository,
            PortfolioRepository portfolioRepository,
            AssetRepository assetRepository
    ) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
    }

    public List<Investment> findAllByPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        return investmentRepository.findAllByPortfolio(portfolio);
    }

    public Investment findById(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        return investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Investment não encontrado para este portfolio")
                );
    }

    @Transactional
    public Investment create(Long portfolioId, Long assetId, Investment investment) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

        investment.setPortfolio(portfolio);
        investment.setAsset(asset);
        return investmentRepository.save(investment);
    }

    @Transactional
    public Investment update(Long portfolioId, Long investmentId, Long assetId, Investment investmentData) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Investment não encontrado para este portfolio")
                );

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

        // Atualizar campos
        investment.setAsset(asset);
        investment.setQuantity(investmentData.getQuantity());
        investment.setAveragePrice(investmentData.getAveragePrice());
        investment.setPurchaseDate(investmentData.getPurchaseDate());
        investment.setNotes(investmentData.getNotes());

        return investment;
    }

    @Transactional
    public void delete(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Investment não encontrado para este portfolio")
                );

        investmentRepository.delete(investment);
    }
}
