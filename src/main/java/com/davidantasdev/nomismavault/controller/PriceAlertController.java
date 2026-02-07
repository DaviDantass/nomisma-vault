package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.service.PriceAlertService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/{userId}/alerts")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;

    public PriceAlertController(PriceAlertService priceAlertService) {
        this.priceAlertService = priceAlertService;
    }

    // GET /api/users/{userId}/alerts?page=0&size=10&sort=createdAt,desc
    @GetMapping
    public ResponseEntity<Page<PriceAlertResponse>> findAllAlertsByUser(
            @PathVariable Long userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                priceAlertService.findAllByUser(userId, pageable));
    }

    // GET /api/users/{userId}/alerts/active?page=0&size=10
    @GetMapping("/active")
    public ResponseEntity<Page<PriceAlertResponse>> findActiveAlertsByUser(
            @PathVariable Long userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                priceAlertService.findActiveByUser(userId, pageable));
    }

    // GET /api/users/{userId}/alerts/{alertId}
    @GetMapping("/{alertId}")
    public ResponseEntity<PriceAlertResponse> findAlertById(
            @PathVariable Long userId,
            @PathVariable Long alertId) {

        return ResponseEntity.ok(
                priceAlertService.findById(userId, alertId));
    }

    // POST /api/users/{userId}/alerts
    @PostMapping
    public ResponseEntity<PriceAlertResponse> createAlert(
            @PathVariable Long userId,
            @Valid @RequestBody PriceAlertRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(priceAlertService.create(userId, request));
    }

    // PATCH /api/users/{userId}/alerts/{alertId}/deactivate
    @PatchMapping("/{alertId}/deactivate")
    public ResponseEntity<PriceAlertResponse> deactivateAlert(
            @PathVariable Long userId,
            @PathVariable Long alertId) {

        return ResponseEntity.ok(
                priceAlertService.deactivate(userId, alertId));
    }

    // PATCH /api/users/{userId}/alerts/{alertId}/price?newPrice=150.00
    @PatchMapping("/{alertId}/price")
    public ResponseEntity<PriceAlertResponse> updateAlertPrice(
            @PathVariable Long userId,
            @PathVariable Long alertId,
            @RequestParam BigDecimal newPrice) {

        return ResponseEntity.ok(
                priceAlertService.updatePrice(userId, alertId, newPrice));
    }

    // DELETE /api/users/{userId}/alerts/{alertId}
    @DeleteMapping("/{alertId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlert(
            @PathVariable Long userId,
            @PathVariable Long alertId) {

        priceAlertService.delete(userId, alertId);
    }
}
