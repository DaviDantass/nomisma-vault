package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.AssetRequest;
import com.davidantasdev.nomismavault.dto.response.AssetResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.mapper.AssetMapper;
import com.davidantasdev.nomismavault.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
    private final AssetMapper assetMapper;

    public AssetController(
            AssetService assetService,
            AssetMapper assetMapper) {
        this.assetService = assetService;
        this.assetMapper = assetMapper;
    }

    // GET /api/assets
    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        List<Asset> assets = assetService.findAll();
        return ResponseEntity.ok(assetMapper.toResponseList(assets));
    }

    // GET /api/assets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable Long id) {
        Asset asset = assetService.findById(id);
        return ResponseEntity.ok(assetMapper.toResponse(asset));
    }

    // GET /api/assets/ticker/{ticker}
    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<AssetResponse> getAssetByTicker(@PathVariable String ticker) {
        Asset asset = assetService.findByTicker(ticker);
        return ResponseEntity.ok(assetMapper.toResponse(asset));
    }

    // POST /api/assets
    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            @Valid @RequestBody AssetRequest request) {

        Asset asset = assetMapper.toEntity(request);
        Asset created = assetService.create(request.categoryId(), asset);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assetMapper.toResponse(created));
    }

    // PUT /api/assets/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {

        Asset assetData = assetMapper.toEntity(request);
        Asset updated = assetService.update(id, request.categoryId(), assetData);
        return ResponseEntity.ok(assetMapper.toResponse(updated));
    }

    // DELETE /api/assets/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
    }
}
