package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.service.PortfolioService;
import jakarta.validation.Valid;
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

    // GET /api/users/{userId}/portfolios
    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> findAll(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                portfolioService.findAllByUserId(userId)
        );
    }

    // GET /api/users/{userId}/portfolios/{portfolioId}
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> findById(
            @PathVariable Long userId,
            @PathVariable Long portfolioId) {

        return ResponseEntity.ok(
                portfolioService.findById(userId, portfolioId)
        );
    }

    // POST /api/users/{userId}/portfolios
    @PostMapping
    public ResponseEntity<PortfolioResponse> create(
            @PathVariable Long userId,
            @Valid @RequestBody PortfolioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(request, userId));
    }

    // PUT /api/users/{userId}/portfolios/{portfolioId}
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> update(
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
    public void delete(
            @PathVariable Long userId,
            @PathVariable Long portfolioId) {

        portfolioService.delete(userId, portfolioId);
    }
}
