package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    // GET /api/portfolios/{portfolioId}/investments
    // GET /api/portfolios/1/investments?page=0&size=10&sort=createdAt,desc
    @GetMapping
    public ResponseEntity<Page<InvestmentResponse>> findAllInvestmentsByPortfolio(
            @PathVariable Long portfolioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                investmentService.findAllByPortfolio(portfolioId, pageable)
        );
    }


    // GET /api/portfolios/{portfolioId}/investments/{investmentId}
    @GetMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponse> findInvestmentById(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId) {
        return ResponseEntity.ok(
                investmentService.findById(portfolioId, investmentId)
        );
    }

    // POST /api/portfolios/{portfolioId}/investments
    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(
            @PathVariable Long portfolioId,
            @Valid @RequestBody InvestmentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(investmentService.create(
                        portfolioId,
                        request.assetId(),
                        request
                ));
    }

    // PUT /api/portfolios/{portfolioId}/investments/{investmentId}
    @PutMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponse> updateInvestment(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId,
            @Valid @RequestBody InvestmentRequest request) {
        return ResponseEntity.ok(
                investmentService.update(
                        portfolioId,
                        investmentId,
                        request.assetId(),
                        request
                )
        );
    }

    // DELETE /api/portfolios/{portfolioId}/investments/{investmentId}
    @DeleteMapping("/{investmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvestment(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId) {

        investmentService.delete(portfolioId, investmentId);
    }
}
