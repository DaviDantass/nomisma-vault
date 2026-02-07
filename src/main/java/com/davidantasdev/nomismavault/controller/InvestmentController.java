package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
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

    /*GET /api/portfolios/1/investments/5/pnl
         ↓
    Controller
         ↓
    Service.getInvestmentWithPnL()
         ↓
    BrapiClient.fetchAssetQuote("PETR4") → preço atual: R$35.50
         ↓
    Investment.calculateProfitLoss(35.50)
         ↓
    Retorna JSON com todos os dados de P&L
    ----------------------
    ---- Comprou 100 ações a R$30 (investiu R$3.000)
Preço atual R$35,50 (vale R$3.550)
Lucro: R$550 (+18,33%)
    {
  "id": 5,
  "assetTicker": "PETR4",
  "quantity": 100.00,
  "averagePrice": 30.00,
  "currentPrice": 35.50,
  "totalInvested": 3000.00,
  "marketValue": 3550.00,
  "profitLoss": 550.00,
  "profitLossPercent": 18.33
}
Se estivesse no prejuízo (preço caiu para R$25):
{
  "id": 5,
  "assetTicker": "PETR4",
  "quantity": 100.00,
  "averagePrice": 30.00,
  "currentPrice": 25.00,
  "totalInvested": 3000.00,
  "marketValue": 2500.00,
  "profitLoss": -500.00,
  "profitLossPercent": -16.67
}
*/

    @GetMapping("/{investmentId}/pnl")
    public ResponseEntity<InvestmentWithPnLResponse> getInvestmentPnL(
            @PathVariable Long portfolioId,
            @PathVariable Long investmentId) {
        return ResponseEntity.ok(
                investmentService.getInvestmentWithPnL(portfolioId, investmentId)
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
