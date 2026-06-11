package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.PortfolioMapper;
import com.davidantasdev.nomismavault.repository.InvestmentRepository;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            UserRepository userRepository,
            InvestmentRepository investmentRepository,
            PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.investmentRepository = investmentRepository;
        this.portfolioMapper = portfolioMapper;
    }

    public Page<PortfolioResponse> findAllByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return portfolioRepository.findAllByUser(user, pageable)
                .map(portfolioMapper::toResponse);
    }

    public PortfolioResponse findById(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio não encontrado para este usuário"));

        return portfolioMapper.toResponse(portfolio);
    }

    public PortfolioSummaryResponse getSummary(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found for this user"));

        List<Investment> investments = investmentRepository.findAllByPortfolio(portfolio);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            BigDecimal currentPrice = investment.getAsset().getCurrentPrice();
            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
                currentPrice = investment.getAveragePrice();
            }

            totalInvested = totalInvested.add(investment.calculateTotalInvested());
            currentValue = currentValue.add(investment.calculateMarketValue(currentPrice));
        }

        BigDecimal profitLoss = currentValue.subtract(totalInvested);
        BigDecimal profitLossPercent = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercent = profitLoss
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return new PortfolioSummaryResponse(
                portfolio.getId(),
                portfolio.getName(),
                investments.size(),
                totalInvested,
                currentValue,
                profitLoss,
                profitLossPercent);
    }

    @Transactional
    public PortfolioResponse createPortfolio(
            PortfolioRequest request,
            Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Portfolio portfolio = portfolioMapper.toEntity(request);
        portfolio.setUser(user);

        return portfolioMapper.toResponse(
                portfolioRepository.save(portfolio));
    }

    @Transactional
    public PortfolioResponse updatePortfolio(
            Long userId,
            Long portfolioId,
            PortfolioRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio não encontrado para este usuário"));

        portfolioMapper.updateEntityFromRequest(request, portfolio);

        return portfolioMapper.toResponse(portfolio);
    }

    @Transactional
    public void delete(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio não encontrado para este usuário"));

        portfolioRepository.delete(portfolio);
    }
}
