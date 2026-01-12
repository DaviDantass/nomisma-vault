package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.PortfolioMapper;
import com.davidantasdev.nomismavault.repository.PortfolioRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            UserRepository userRepository,
            PortfolioMapper portfolioMapper
    ) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.portfolioMapper = portfolioMapper;
    }


    public Page<PortfolioResponse> findAllByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado")
                );

        return portfolioRepository.findAllByUser(user, pageable)
                .map(portfolioMapper::toResponse);
    }

    public PortfolioResponse findById(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado")
                );

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado para este usuário")
                );

        return portfolioMapper.toResponse(portfolio);
    }

    @Transactional
    public PortfolioResponse createPortfolio(
            PortfolioRequest request,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado")
                );

        Portfolio portfolio = portfolioMapper.toEntity(request);
        portfolio.setUser(user);

        return portfolioMapper.toResponse(
                portfolioRepository.save(portfolio)
        );
    }

    @Transactional
    public PortfolioResponse updatePortfolio(
            Long userId,
            Long portfolioId,
            PortfolioRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado")
                );

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado para este usuário")
                );

        portfolioMapper.updateEntityFromRequest(request, portfolio);

        return portfolioMapper.toResponse(portfolio);
    }

    @Transactional
    public void delete(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado")
                );

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(portfolioId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio não encontrado para este usuário")
                );

        portfolioRepository.delete(portfolio);
    }
}


