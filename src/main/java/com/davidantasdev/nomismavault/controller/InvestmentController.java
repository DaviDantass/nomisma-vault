package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.entity.Investment;
import com.davidantasdev.nomismavault.mapper.InvestmentMapper;
import com.davidantasdev.nomismavault.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/investments")
public class InvestmentController {

    private final InvestmentService investmentService;
    private final InvestmentMapper investmentMapper;

    public InvestmentController(
            InvestmentService investmentService,
            InvestmentMapper investmentMapper) {
        this.investmentService = investmentService;
        this.investmentMapper = investmentMapper;
    }

    // GET /api/portfolios/{portfolioId}/investments
    @GetMapping
    public ResponseEntity<List<InvestmentResponse>> getAllInvestments(
            @PathVariable Long portfolioId) {

        List<Investment> investments = investmentService.findAllByPortfolio(portfolioId);
        return ResponseEntity.ok(investmentMapper.toResponseList(investments));
    }

    // GET /api/portfolios/{portfolioId}/investments/{investmentId}
    @GetMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponse> getInvestmentById(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId) {

        Investment investment = investmentService.findById(portfolioId, investmentId);
        return ResponseEntity.ok(investmentMapper.toResponse(investment));
    }

    // POST /api/portfolios/{portfolioId}/investments
    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(
            @PathVariable Long portfolioId,
            @Valid @RequestBody InvestmentRequest request) {

        Investment investment = investmentMapper.toEntity(request);
        Investment created = investmentService.create(portfolioId, request.assetId(), investment);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(investmentMapper.toResponse(created));
    }

    // PUT /api/portfolios/{portfolioId}/investments/{investmentId}
    @PutMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponse> updateInvestment(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId,
            @Valid @RequestBody InvestmentRequest request) {

        Investment investmentData = investmentMapper.toEntity(request);
        Investment updated = investmentService.update(portfolioId, investmentId, request.assetId(), investmentData);
        return ResponseEntity.ok(investmentMapper.toResponse(updated));
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
