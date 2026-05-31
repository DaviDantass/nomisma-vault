package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.PortfolioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolios", description = "Gestão de carteiras de investimentos")
public class PortfolioController {

        private final PortfolioService portfolioService;
        private final AuthenticatedUserProvider authenticatedUserProvider;

        public PortfolioController(
                        PortfolioService portfolioService,
                        AuthenticatedUserProvider authenticatedUserProvider) {
                this.portfolioService = portfolioService;
                this.authenticatedUserProvider = authenticatedUserProvider;
        }

        @GetMapping("/paginated")
        public ResponseEntity<Page<PortfolioResponse>> findAllPortfoliosByUser(
                        Pageable pageable) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                portfolioService.findAllByUser(userId, pageable));
        }

        @GetMapping("/{portfolioId}")
        public ResponseEntity<PortfolioResponse> findPortfolioById(
                        @PathVariable Long portfolioId) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                portfolioService.findById(userId, portfolioId));
        }

        @PostMapping
        public ResponseEntity<PortfolioResponse> createPortfolio(
                        @Valid @RequestBody PortfolioRequest request) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                portfolioService.createPortfolio(request, userId));
        }

        @PutMapping("/{portfolioId}")
        public ResponseEntity<PortfolioResponse> updatePortfolio(
                        @PathVariable Long portfolioId,
                        @Valid @RequestBody PortfolioRequest request) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                return ResponseEntity.ok(
                                portfolioService.updatePortfolio(userId, portfolioId, request));
        }

        @DeleteMapping("/{portfolioId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deletePortfolio(
                        @PathVariable Long portfolioId) {
                Long userId = authenticatedUserProvider.getCurrentUserId();

                portfolioService.delete(userId, portfolioId);
        }
}
