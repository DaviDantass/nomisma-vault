package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.PriceAlertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Price Alerts", description = "Alertas de preço para ativos")
public class PriceAlertController {

        private final PriceAlertService priceAlertService;
        private final AuthenticatedUserProvider authenticatedUserProvider;

        public PriceAlertController(
                        PriceAlertService priceAlertService,
                        AuthenticatedUserProvider authenticatedUserProvider) {
                this.priceAlertService = priceAlertService;
                this.authenticatedUserProvider = authenticatedUserProvider;
        }

        @GetMapping
        public ResponseEntity<Page<PriceAlertResponse>> findAllAlertsByUser(
                        Pageable pageable) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                priceAlertService.findAllByUser(userId, pageable));
        }

        @GetMapping("/active")
        public ResponseEntity<Page<PriceAlertResponse>> findActiveAlertsByUser(
                        Pageable pageable) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                priceAlertService.findActiveByUser(userId, pageable));
        }

        @GetMapping("/{alertId}")
        public ResponseEntity<PriceAlertResponse> findAlertById(
                        @PathVariable Long alertId) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                priceAlertService.findById(userId, alertId));
        }

        @PostMapping
        public ResponseEntity<PriceAlertResponse> createAlert(
                        @Valid @RequestBody PriceAlertRequest request) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(priceAlertService.create(userId, request));
        }

        @PatchMapping("/{alertId}/deactivate")
        public ResponseEntity<PriceAlertResponse> deactivateAlert(
                        @PathVariable Long alertId) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                priceAlertService.deactivate(userId, alertId));
        }

        @PatchMapping("/{alertId}/price")
        public ResponseEntity<PriceAlertResponse> updateAlertPrice(
                        @PathVariable Long alertId,
                        @RequestParam BigDecimal newPrice) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                priceAlertService.updatePrice(userId, alertId, newPrice));
        }

        @DeleteMapping("/{alertId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteAlert(
                        @PathVariable Long alertId) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                priceAlertService.delete(userId, alertId);
        }
}
