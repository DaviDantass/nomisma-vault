package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // GET /api/users/{userId}/portfolios/paginated?page=0&size=10&sort=createdAt,desc
    @GetMapping("/paginated")
    public ResponseEntity<Page<PortfolioResponse>> findAllPortfoliosByUser(
            @PathVariable Long userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                portfolioService.findAllByUser(userId, pageable)
        );
    }

    // GET /api/users/{userId}/portfolios/{portfolioId}
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> findPortfolioById(
            @PathVariable Long userId,
            @PathVariable Long portfolioId) {

        return ResponseEntity.ok(
                portfolioService.findById(userId, portfolioId)
        );
    }

    // POST /api/users/{userId}/portfolios
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @PathVariable Long userId,
            @Valid @RequestBody PortfolioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        portfolioService.createPortfolio(request, userId)
                );
    }

    // PUT /api/users/{userId}/portfolios/{portfolioId}
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(
            @PathVariable Long userId,
            @PathVariable Long portfolioId,
            @Valid @RequestBody PortfolioRequest request) {

        return ResponseEntity.ok(
                portfolioService.updatePortfolio(userId, portfolioId, request)
        );
    }

    // DELETE /api/users/{userId}/portfolios/{portfolioId}
    @DeleteMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable Long userId,
            @PathVariable Long portfolioId) {

        portfolioService.delete(userId, portfolioId);
    }
}
