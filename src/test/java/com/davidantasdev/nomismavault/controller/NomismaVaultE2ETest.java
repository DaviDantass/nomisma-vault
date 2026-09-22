package com.davidantasdev.nomismavault.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.davidantasdev.nomismavault.dto.response.AssetQuoteDTO;
import com.davidantasdev.nomismavault.exception.MarketDataUnavailableException;
import com.davidantasdev.nomismavault.marketdata.economic.EconomicIndicatorProvider;
import com.davidantasdev.nomismavault.marketdata.provider.MarketDataProvider;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NomismaVaultE2ETest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private MarketDataProvider marketDataProvider;

  @MockitoBean private EconomicIndicatorProvider economicIndicatorProvider;

  @Test
  void rootHealthAndCurrentUserPortfolioRoutesWorkWithoutUserIdInUrl() throws Exception {
    AuthenticatedUser user = registerAndLogin("me." + System.nanoTime() + "@example.com");

    mockMvc.perform(get("/")).andExpect(status().isOk());

    MvcResult portfolio =
        mockMvc
            .perform(
                post("/me/portfolios")
                    .header("Authorization", "Bearer " + user.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Minha carteira\",\"description\":\"Sem id do usuario\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Minha carteira"))
            .andReturn();
    int portfolioId = JsonPath.read(portfolio.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(get("/me/portfolios").header("Authorization", "Bearer " + user.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(portfolioId));

    mockMvc
        .perform(get("/me/dashboard").header("Authorization", "Bearer " + user.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.portfoliosCount").value(1))
        .andExpect(jsonPath("$.portfolios[0].portfolioId").value(portfolioId));
  }

  @Test
  void userProfileUsesMeRouteAndProtectsLegacyUserRoutes() throws Exception {
    String suffix = String.valueOf(System.nanoTime());
    AuthenticatedUser owner = registerAndLogin("profile.owner." + suffix + "@example.com");
    AuthenticatedUser intruder = registerAndLogin("profile.intruder." + suffix + "@example.com");

    mockMvc
        .perform(get("/me").header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(owner.userId()))
        .andExpect(jsonPath("$.email").value("profile.owner." + suffix + "@example.com"));

    mockMvc
        .perform(
            get("/users/" + owner.userId()).header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            get("/users/email/profile.owner." + suffix + "@example.com")
                .header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(get("/users").header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isForbidden());
  }

  @Test
  void currentUserCanManageAlertsAndPaginatedResponsesHaveStableMetadata() throws Exception {
    String suffix = String.valueOf(System.nanoTime());
    AuthenticatedUser owner = registerAndLogin("alert.owner." + suffix + "@example.com");
    AuthenticatedUser intruder = registerAndLogin("alert.intruder." + suffix + "@example.com");

    MvcResult category =
        mockMvc
            .perform(
                post("/categories")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Alert category " + suffix + "\",\"riskLevel\":\"LOW\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    int categoryId = JsonPath.read(category.getResponse().getContentAsString(), "$.id");

    MvcResult asset =
        mockMvc
            .perform(
                post("/assets")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"ticker\":\"AL"
                            + suffix
                            + "\",\"name\":\"Alert asset\",\"categoryId\":"
                            + categoryId
                            + ",\"currentPrice\":10.00}"))
            .andExpect(status().isCreated())
            .andReturn();
    int assetId = JsonPath.read(asset.getResponse().getContentAsString(), "$.id");

    MvcResult alert =
        mockMvc
            .perform(
                post("/me/alerts")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"assetId\":"
                            + assetId
                            + ",\"targetPrice\":12.50,\"condition\":\"ABOVE\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(owner.userId()))
            .andReturn();
    int alertId = JsonPath.read(alert.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(get("/me/alerts?page=0&size=5").header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(alertId))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true));

    mockMvc
        .perform(get("/me/alerts/" + alertId).header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(
            patch("/me/alerts/" + alertId + "/deactivate")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isActive").value(false));
  }

  @Test
  void authenticatedUserCanReadSupportedEconomicIndicator() throws Exception {
    AuthenticatedUser user = registerAndLogin("economic." + System.nanoTime() + "@example.com");
    when(economicIndicatorProvider.getLatest("11"))
        .thenReturn(
            new EconomicIndicatorProvider.EconomicObservation(
                new BigDecimal("14.15"), LocalDate.of(2026, 9, 18)));

    mockMvc
        .perform(get("/market/indicators/selic").header("Authorization", "Bearer " + user.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.indicator").value("selic"))
        .andExpect(jsonPath("$.seriesCode").value("11"))
        .andExpect(jsonPath("$.name").value("SELIC diária"))
        .andExpect(jsonPath("$.value").value(14.15))
        .andExpect(jsonPath("$.referenceDate").value("2026-09-18"));
  }

  @Test
  void authenticatedUserCanManageCategoryAndAssetEndToEnd() throws Exception {
    String suffix = String.valueOf(System.nanoTime());
    String email = "e2e." + suffix + "@example.com";
    String password = "StrongPass123";

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\"E2E User\",\"email\":\""
                        + email
                        + "\",\"password\":\""
                        + password
                        + "\"}"))
        .andExpect(status().isCreated());

    MvcResult login =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn();
    String token = JsonPath.read(login.getResponse().getContentAsString(), "$.token");
    assertNotNull(token);

    MvcResult category =
        mockMvc
            .perform(
                post("/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"E2E Category "
                            + suffix
                            + "\",\"description\":\"Integration\",\"riskLevel\":\"LOW\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andReturn();
    int categoryId = JsonPath.read(category.getResponse().getContentAsString(), "$.id");

    MvcResult asset =
        mockMvc
            .perform(
                post("/assets")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"ticker\":\"E2E"
                            + suffix
                            + "\",\"name\":\"E2E Asset\",\"categoryId\":"
                            + categoryId
                            + ",\"currentPrice\":10.50}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ticker").value("E2E" + suffix))
            .andReturn();
    int assetId = JsonPath.read(asset.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(get("/assets/" + assetId).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("E2E Asset"));

    mockMvc
        .perform(
            put("/assets/" + assetId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"ticker\":\"E2E"
                        + suffix
                        + "\",\"name\":\"Updated Asset\",\"categoryId\":"
                        + categoryId
                        + ",\"currentPrice\":12.75}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Asset"));

    mockMvc
        .perform(delete("/assets/" + assetId).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/assets/" + assetId).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @Test
  void protectedResourcesRejectInvalidToken() throws Exception {
    mockMvc
        .perform(get("/categories").header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  void priceHistoryWriteEndpointsAreNotAvailableToRegularUsers() throws Exception {
    AuthenticatedUser user = registerAndLogin("history." + System.nanoTime() + "@example.com");

    mockMvc
        .perform(
            post("/price-history")
                .header("Authorization", "Bearer " + user.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assetId\":1,\"price\":10.00,\"date\":\"2026-09-21\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void userCannotAccessAnotherUsersPortfoliosByChangingThePathId() throws Exception {
    String suffix = String.valueOf(System.nanoTime());
    AuthenticatedUser owner = registerAndLogin("owner." + suffix + "@example.com");
    AuthenticatedUser intruder = registerAndLogin("intruder." + suffix + "@example.com");

    MvcResult portfolio =
        mockMvc
            .perform(
                post("/users/" + owner.userId() + "/portfolios")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Private portfolio\",\"description\":\"Owner only\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    int portfolioId = JsonPath.read(portfolio.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(
            get("/users/" + owner.userId() + "/portfolios/paginated")
                .header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/investments")
                .header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/transactions")
                .header("Authorization", "Bearer " + intruder.token()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  void marketQuoteUsesInternalProviderAndCachesRepeatedRequests() throws Exception {
    AuthenticatedUser user = registerAndLogin("market." + System.nanoTime() + "@example.com");
    AssetQuoteDTO quote =
        new AssetQuoteDTO(
            "PETR4",
            new BigDecimal("38.50"),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            LocalDateTime.now());
    when(marketDataProvider.getQuote("PETR4")).thenReturn(quote);

    for (int request = 0; request < 2; request++) {
      mockMvc
          .perform(get("/market/quote/petr4").header("Authorization", "Bearer " + user.token()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.ticker").value("PETR4"))
          .andExpect(jsonPath("$.price").value(38.50));
    }

    verify(marketDataProvider, times(1)).getQuote("PETR4");
  }

  @Test
  void userCanBuyAndPartiallySellAnAssetButCannotSellMoreThanOwned() throws Exception {
    String suffix = String.valueOf(System.nanoTime());
    AuthenticatedUser owner = registerAndLogin("trader." + suffix + "@example.com");
    AuthenticatedUser intruder = registerAndLogin("intruder.trader." + suffix + "@example.com");

    MvcResult category =
        mockMvc
            .perform(
                post("/categories")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Trading " + suffix + "\",\"riskLevel\":\"HIGH\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    int categoryId = JsonPath.read(category.getResponse().getContentAsString(), "$.id");

    MvcResult asset =
        mockMvc
            .perform(
                post("/assets")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"ticker\":\"TR"
                            + suffix
                            + "\",\"name\":\"Trading Asset\",\"categoryId\":"
                            + categoryId
                            + ",\"currentPrice\":20.00}"))
            .andExpect(status().isCreated())
            .andReturn();
    int assetId = JsonPath.read(asset.getResponse().getContentAsString(), "$.id");

    MvcResult portfolio =
        mockMvc
            .perform(
                post("/users/" + owner.userId() + "/portfolios")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Trading portfolio\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    int portfolioId = JsonPath.read(portfolio.getResponse().getContentAsString(), "$.id");

    MvcResult buy =
        mockMvc
            .perform(
                post("/portfolios/" + portfolioId + "/transactions")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        transactionPayload(portfolioId, assetId, "BUY", "10", "20.00", "1.50")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(201.50))
            .andReturn();
    int buyId = JsonPath.read(buy.getResponse().getContentAsString(), "$.id");

    MvcResult investments =
        mockMvc
            .perform(
                get("/portfolios/" + portfolioId + "/investments")
                    .header("Authorization", "Bearer " + owner.token()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].quantity").value(10))
            .andExpect(jsonPath("$.content[0].averagePrice").value(20.00))
            .andReturn();
    int investmentId =
        JsonPath.read(investments.getResponse().getContentAsString(), "$.content[0].id");
    when(marketDataProvider.getQuote("TR" + suffix))
        .thenThrow(new MarketDataUnavailableException("Market unavailable", null));
    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/investments/" + investmentId + "/pnl")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPrice").value(20.00))
        .andExpect(jsonPath("$.usingFallbackPrice").value(true));

    mockMvc
        .perform(
            get("/me/portfolios/" + portfolioId + "/summary")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.positionsCount").value(1));
    mockMvc
        .perform(get("/me/dashboard").header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.portfoliosCount").value(1));

    MvcResult sell =
        mockMvc
            .perform(
                post("/portfolios/" + portfolioId + "/transactions")
                    .header("Authorization", "Bearer " + owner.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(transactionPayload(portfolioId, assetId, "SELL", "4", "25.00", "0")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(100.00))
            .andReturn();
    int sellId = JsonPath.read(sell.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/investments")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].quantity").value(6))
        .andExpect(jsonPath("$.content[0].averagePrice").value(20.00));

    mockMvc
        .perform(
            post("/portfolios/" + portfolioId + "/transactions")
                .header("Authorization", "Bearer " + owner.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionPayload(portfolioId, assetId, "SELL", "7", "25.00", "0")))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            post("/portfolios/" + portfolioId + "/transactions")
                .header("Authorization", "Bearer " + intruder.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionPayload(portfolioId, assetId, "BUY", "1", "20.00", "0")))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            delete("/portfolios/" + portfolioId + "/transactions/" + sellId)
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/investments")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].quantity").value(10));

    mockMvc
        .perform(
            delete("/portfolios/" + portfolioId + "/transactions/" + buyId)
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(
            get("/portfolios/" + portfolioId + "/investments")
                .header("Authorization", "Bearer " + owner.token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  private AuthenticatedUser registerAndLogin(String email) throws Exception {
    MvcResult registration =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"E2E User\",\"email\":\""
                            + email
                            + "\",\"password\":\"StrongPass123\"}"))
            .andExpect(status().isCreated())
            .andReturn();
    int userId = JsonPath.read(registration.getResponse().getContentAsString(), "$.id");

    MvcResult login =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\",\"password\":\"StrongPass123\"}"))
            .andExpect(status().isOk())
            .andReturn();
    String token = JsonPath.read(login.getResponse().getContentAsString(), "$.token");
    return new AuthenticatedUser(userId, token);
  }

  private record AuthenticatedUser(int userId, String token) {}

  private String transactionPayload(
      int portfolioId, int assetId, String type, String quantity, String price, String fees) {
    return "{\"portfolioId\":"
        + portfolioId
        + ",\"assetId\":"
        + assetId
        + ",\"type\":\""
        + type
        + "\",\"quantity\":"
        + quantity
        + ",\"price\":"
        + price
        + ",\"transactionDate\":\"2026-09-21\",\"fees\":"
        + fees
        + "}";
  }
}
