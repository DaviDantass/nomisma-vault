package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.AssetRequest;
import com.davidantasdev.nomismavault.dto.response.AssetResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.AssetMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final InvestmentCategoryRepository investmentCategoryRepository;
    private final AssetMapper assetMapper;

    public AssetService(
            AssetRepository assetRepository,
            InvestmentCategoryRepository investmentCategoryRepository,
            AssetMapper assetMapper
    ) {
        this.assetRepository = assetRepository;
        this.investmentCategoryRepository = investmentCategoryRepository;
        this.assetMapper = assetMapper;
    }

    public List<AssetResponse> findAll() {
        return assetMapper.toResponseList(assetRepository.findAll());
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );
        return assetMapper.toResponse(asset);
    }

    public AssetResponse findByTicker(String ticker) {
        Asset asset = assetRepository.findByTicker(ticker)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset com ticker " + ticker + " não encontrado")
                );
        return assetMapper.toResponse(asset);
    }

    @Transactional
    public AssetResponse create(Long categoryId, AssetRequest request) {
        Asset asset = assetMapper.toEntity(request);
        
        if (assetRepository.existsByTicker(asset.getTicker())) {
            throw new BusinessException("Já existe um asset com o ticker " + asset.getTicker());
        }

        InvestmentCategory category = investmentCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categoria não encontrada")
                );

        asset.setCategory(category);
        asset.setLastUpdate(LocalDateTime.now());
        Asset saved = assetRepository.save(asset);
        return assetMapper.toResponse(saved);
    }

    @Transactional
    public AssetResponse update(Long id, Long categoryId, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

        Asset assetData = assetMapper.toEntity(request);
        
        // Verificar se o ticker já existe em outro asset
        if (!asset.getTicker().equals(assetData.getTicker()) &&
                assetRepository.existsByTicker(assetData.getTicker())) {
            throw new BusinessException("Já existe um asset com o ticker " + assetData.getTicker());
        }

        InvestmentCategory category = investmentCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categoria não encontrada")
                );

        asset.setTicker(assetData.getTicker());
        asset.setName(assetData.getName());
        asset.setCategory(category);
        asset.setCurrentPrice(assetData.getCurrentPrice());
        asset.setLastUpdate(LocalDateTime.now());

        return assetMapper.toResponse(asset);
    }

    @Transactional
    public void delete(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

        assetRepository.delete(asset);
    }
}
