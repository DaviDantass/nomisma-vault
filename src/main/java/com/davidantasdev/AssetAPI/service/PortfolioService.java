package com.davidantasdev.AssetAPI.service;

import com.davidantasdev.AssetAPI.dto.request.PortfolioRequest;
import com.davidantasdev.AssetAPI.dto.response.PortfolioResponse;
import com.davidantasdev.AssetAPI.entity.Portfolio;
import com.davidantasdev.AssetAPI.entity.User;
import com.davidantasdev.AssetAPI.exception.ResourceNotFoundException;
import com.davidantasdev.AssetAPI.mapper.PortfolioMapper;
import com.davidantasdev.AssetAPI.repository.PortfolioRepository;
import com.davidantasdev.AssetAPI.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository, PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.portfolioMapper = portfolioMapper;
    }

    public PortfolioResponse findById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id).get();
        return portfolioMapper.toResponse(portfolio);
    }

    public List<PortfolioResponse> findAll() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        return portfolioMapper.toResponseList(portfolios);
    }

    public List<PortfolioResponse> findAllByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId é obrigatório");
        }
        return portfolioMapper.toResponseList(
                portfolioRepository.findAllByUserId(userId)
        );
    }

    @Transactional
    public PortfolioResponse createPortfolio(PortfolioRequest portfolioRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Portfolio portfolio = portfolioMapper.toEntity(portfolioRequest);
        portfolio.setUser(user);

        return portfolioMapper.toResponse(
                portfolioRepository.save(portfolio)
        );
    }
    @Transactional
    public PortfolioResponse updatePortfolio(Long userId, Long portfolioId, PortfolioRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("PortfolioRequest não pode ser null");
        }

        Portfolio portfolio = portfolioRepository
                .findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado para este usuário")
                );

        portfolioMapper.updateEntityFromRequest(request, portfolio);

        if (portfolio.getName() != null) {
            portfolio.setName(portfolio.getName().trim());
        }
        if (portfolio.getDescription() != null) {
            portfolio.setDescription(portfolio.getDescription().trim());
        }

        return portfolioMapper.toResponse(portfolio);
    }

    @Transactional
    public void delete(Long userId, Long portfolioId) {

        Portfolio portfolio = portfolioRepository
                .findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado para este usuário")
                );

        portfolioRepository.delete(portfolio);
    }


}
