package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PriceHistoryRequest;
import com.davidantasdev.nomismavault.dto.response.PriceHistoryResponse;
import com.davidantasdev.nomismavault.service.PriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/price-history")
@Tag(name = "Price History", description = "Histórico de cotações dos ativos")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<PriceHistoryResponse>> findAll() {
        return ResponseEntity.ok(priceHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceHistoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(priceHistoryService.findById(id));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<PriceHistoryResponse>> findByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(priceHistoryService.findByAssetId(assetId));
    }

    @GetMapping("/asset/{assetId}/range")
    public ResponseEntity<List<PriceHistoryResponse>> findByAssetIdAndDateRange(
            @PathVariable Long assetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(priceHistoryService.findByAssetIdAndDateRange(assetId, startDate, endDate));
    }

    @GetMapping("/asset/{assetId}/latest")
    public ResponseEntity<PriceHistoryResponse> findLatestByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(priceHistoryService.findLatestByAssetId(assetId));
    }

    @PostMapping
    public ResponseEntity<PriceHistoryResponse> create(@Valid @RequestBody PriceHistoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(priceHistoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceHistoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PriceHistoryRequest request) {
        return ResponseEntity.ok(priceHistoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
