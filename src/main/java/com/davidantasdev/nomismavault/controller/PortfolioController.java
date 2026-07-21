package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/portfolios")
@Tag(
        name = "Portfolios",
        description = "Gestão de carteiras"
)public class PortfolioController {

        private final PortfolioService portfolioService;

        public PortfolioController(PortfolioService portfolioService) {
                this.portfolioService = portfolioService;
        }

        @GetMapping("/paginated")
        public ResponseEntity<Page<PortfolioResponse>> findAllPortfoliosByUser(
                        @PathVariable Long userId,
                        Pageable pageable) {

                return ResponseEntity.ok(
                                portfolioService.findAllByUser(userId, pageable));
        }

        @GetMapping("/{portfolioId}")
        public ResponseEntity<PortfolioResponse> findPortfolioById(
                        @PathVariable Long userId,
                        @PathVariable Long portfolioId) {

                return ResponseEntity.ok(
                                portfolioService.findById(userId, portfolioId));
        }

        @GetMapping("/{portfolioId}/summary")
        public ResponseEntity<PortfolioSummaryResponse> getPortfolioSummary(
                        @PathVariable Long userId,
                        @PathVariable Long portfolioId) {

                return ResponseEntity.ok(
                                portfolioService.getSummary(userId, portfolioId));
        }

        @PostMapping
        public ResponseEntity<PortfolioResponse> createPortfolio(
                        @PathVariable Long userId,
                        @Valid @RequestBody PortfolioRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                portfolioService.createPortfolio(request, userId));
        }

        @PutMapping("/{portfolioId}")
        public ResponseEntity<PortfolioResponse> updatePortfolio(
                        @PathVariable Long userId,
                        @PathVariable Long portfolioId,
                        @Valid @RequestBody PortfolioRequest request) {

                return ResponseEntity.ok(
                                portfolioService.updatePortfolio(userId, portfolioId, request));
        }

        @DeleteMapping("/{portfolioId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deletePortfolio(
                        @PathVariable Long userId,
                        @PathVariable Long portfolioId) {

                portfolioService.delete(userId, portfolioId);
        }
}
