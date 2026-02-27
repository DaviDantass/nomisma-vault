package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.service.PriceAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/{userId}/alerts")
@Tag(name = "Price Alerts", description = "Alertas de preço para ativos")
public class PriceAlertController {

        private final PriceAlertService priceAlertService;

        public PriceAlertController(PriceAlertService priceAlertService) {
                this.priceAlertService = priceAlertService;
        }

        @GetMapping
        public ResponseEntity<Page<PriceAlertResponse>> findAllAlertsByUser(
                        @PathVariable Long userId,
                        Pageable pageable) {

                return ResponseEntity.ok(
                                priceAlertService.findAllByUser(userId, pageable));
        }

        @GetMapping("/active")
        public ResponseEntity<Page<PriceAlertResponse>> findActiveAlertsByUser(
                        @PathVariable Long userId,
                        Pageable pageable) {

                return ResponseEntity.ok(
                                priceAlertService.findActiveByUser(userId, pageable));
        }

        @GetMapping("/{alertId}")
        public ResponseEntity<PriceAlertResponse> findAlertById(
                        @PathVariable Long userId,
                        @PathVariable Long alertId) {

                return ResponseEntity.ok(
                                priceAlertService.findById(userId, alertId));
        }

        @PostMapping
        public ResponseEntity<PriceAlertResponse> createAlert(
                        @PathVariable Long userId,
                        @Valid @RequestBody PriceAlertRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(priceAlertService.create(userId, request));
        }

        @PatchMapping("/{alertId}/deactivate")
        public ResponseEntity<PriceAlertResponse> deactivateAlert(
                        @PathVariable Long userId,
                        @PathVariable Long alertId) {

                return ResponseEntity.ok(
                                priceAlertService.deactivate(userId, alertId));
        }

        @PatchMapping("/{alertId}/price")
        public ResponseEntity<PriceAlertResponse> updateAlertPrice(
                        @PathVariable Long userId,
                        @PathVariable Long alertId,
                        @RequestParam BigDecimal newPrice) {

                return ResponseEntity.ok(
                                priceAlertService.updatePrice(userId, alertId, newPrice));
        }

        @DeleteMapping("/{alertId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteAlert(
                        @PathVariable Long userId,
                        @PathVariable Long alertId) {

                priceAlertService.delete(userId, alertId);
        }
}
