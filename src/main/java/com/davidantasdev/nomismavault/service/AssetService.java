package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
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

    public AssetService(
            AssetRepository assetRepository,
            InvestmentCategoryRepository investmentCategoryRepository
    ) {
        this.assetRepository = assetRepository;
        this.investmentCategoryRepository = investmentCategoryRepository;
    }

    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    public Asset findById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );
    }

    public Asset findByTicker(String ticker) {
        return assetRepository.findByTicker(ticker)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset com ticker " + ticker + " não encontrado")
                );
    }

    @Transactional
    public Asset create(Long categoryId, Asset asset) {
        if (assetRepository.existsByTicker(asset.getTicker())) {
            throw new BusinessException("Já existe um asset com o ticker " + asset.getTicker());
        }

        InvestmentCategory category = investmentCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categoria não encontrada")
                );

        asset.setCategory(category);
        asset.setLastUpdate(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset update(Long id, Long categoryId, Asset assetData) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset não encontrado")
                );

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

        return asset;
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
