package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.AssetRequest;
import com.davidantasdev.nomismavault.dto.response.AssetResponse;
import com.davidantasdev.nomismavault.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets", description = "Cadastro de ativos (ações, FIIs, cripto, renda fixa)")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> findAllAssets() {
        return ResponseEntity.ok(assetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> findAssetById(
            @PathVariable Long id) {
        return ResponseEntity.ok(assetService.findById(id));
    }

    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<AssetResponse> findAssetByTicker(
            @PathVariable String ticker) {
        return ResponseEntity.ok(assetService.findByTicker(ticker));
    }

    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assetService.create(request.categoryId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(
                assetService.update(id, request.categoryId(), request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(
            @PathVariable Long id) {

        assetService.delete(id);
    }
}
