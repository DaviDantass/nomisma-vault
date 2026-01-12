package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final InvestmentMapper investmentMapper;

    public InvestmentService(
            InvestmentRepository investmentRepository,
            PortfolioRepository portfolioRepository,
            AssetRepository assetRepository,
            InvestmentMapper investmentMapper
    ) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.investmentMapper = investmentMapper;
    }

    public Page<InvestmentResponse> findAllByPortfolio(Long portfolioId, Pageable pageable) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        return investmentRepository.findAllByPortfolio(portfolio, pageable)
                .map(investmentMapper::toResponse);
    }

    public InvestmentResponse findById(Long portfolioId, Long investmentId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        Investment investment = investmentRepository.findByIdAndPortfolio(investmentId, portfolio)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Investment não encontrado para este portfolio")
                );
        
        return investmentMapper.toResponse(investment);
    }

    @Transactional
    public InvestmentResponse create(Long portfolioId, Long assetId, InvestmentRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado")
                );

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

        Investment investment = investmentMapper.toEntity(request);
        investment.setPortfolio(portfolio);
        investment.setAsset(asset);
        Investment saved = investmentRepository.save(investment);
        return investmentMapper.toResponse(saved);
    }

    @Transactional
    public InvestmentResponse update(Long portfolioId, Long investmentId, Long assetId, InvestmentRequest request) {
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

        Investment investmentData = investmentMapper.toEntity(request);
        
        // Atualizar campos
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
