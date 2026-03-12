<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge" alt="Status"/>
</p>

<h1 align="center">💰 NomismaVault</h1>

<p align="center">
  <strong>Professional REST API for financial portfolio management with real-time B3 market integration</strong>
</p>

<p align="center">
  <a href="#-overview">Overview</a> •
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Stack</a> •
  <a href="#-getting-started">Setup</a> •
  <a href="#-api-documentation">API</a> •
  <a href="#-status">Status</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

---

## 📋 Overview

**NomismaVault** is a production-grade RESTful API for personal financial asset tracking, portfolio management, and real-time performance monitoring. Built following software engineering best practices with a focus on clean architecture, security, and scalability.

> *"Nomisma"* (νόμισμα) means "coin" in ancient Greek — the etymological origin of **numismatics**

### 🎯 Core Capabilities

- **Multi-portfolio management** with isolated user contexts
- **Real-time P&L calculation** via Brapi integration (B3 Brazilian stock exchange)
- **Intelligent price tracking** with automated schedulers (daily updates, 5-min alerts)
- **JWT-based security** with BCrypt password encryption
- **Financial calculations** (average price, profitability, position tracking)
- **Docker-ready deployment** with orchestrated PostgreSQL
- **Comprehensive API** with 10+ REST endpoints and Swagger documentation

---

## ✨ Features

### Implemented ✅

| Feature | Details |
|---------|---------|
| **User Management** | Full CRUD, email validation, secure authentication |
| **Authentication** | JWT tokens, stateless sessions, role-ready security |
| **Portfolios** | Multiple portfolios per user, pagination, sorting |
| **Investments** | Position tracking, real-time P&L, automatic average price calculation |
| **Transactions** | Buy/sell history, date filtering, fee tracking |
| **Price Alerts** | Conditional alerts (ABOVE/BELOW), automated checks every 5 minutes |
| **Asset Catalog** | Stocks, REITs, crypto, fixed income with risk categorization |
| **Brapi Integration** | Live B3 quotes, request caching, fallback error handling |
| **Schedulers** | Automated price updates (6 PM) and alert monitoring (10 AM - 6 PM) |
| **Global Error Handling** | Standardized API error responses |
| **Data Migrations** | Versioned schemas with Flyway |

### Planned ⏳

- Dashboard with aggregated portfolio metrics
- Category diversification charts
- Performance comparison (CDI/IPCA benchmarks)
- Comprehensive test suite (80%+ coverage)
- Role-based authorization
- Rate limiting


## 🛠️ Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Runtime** | Java (Eclipse Temurin) | 21 LTS |
| **Framework** | Spring Boot | 4.0.1 |
| **ORM** | Spring Data JPA + Hibernate | - |
| **Database** | PostgreSQL | 15 |
| **Validation** | Jakarta Bean Validation | - |
| **Mapping** | MapStruct | 1.5.5 |
| **Security** | Spring Security + JWT (Auth0) | 4.2.1 |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) | 2.8.5 |
| **Cache** | Spring Cache | - |
| **DB Migrations** | Flyway | - |
| **Container** | Docker + Docker Compose | - |
| **Build** | Maven | 3.9+ |

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose (easiest) **OR**
- Java 21, Maven 3.9+, PostgreSQL 15

### Quick Start (Docker - Recommended)

```bash
git clone https://github.com/davidantasdev/nomisma-vault.git
cd nomisma-vault
docker-compose up -d

# API available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Local Development

```bash
# Create database
createdb nomismavault

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Environment Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/nomismavault` |
| `SPRING_DATASOURCE_USERNAME` | Database user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `manager` |
| `BRAPI_API_URL` | Brapi base URL | `https://brapi.dev/api` |
| `BRAPI_API_TOKEN` | Brapi API token (optional) | - |
| `JWT_SECRET` | JWT signing secret | - |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` (24h) |

---

## 📚 API Documentation

**Live Interactive Docs:** http://localhost:8080/swagger-ui.html

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register new user |
| `POST` | `/auth/login` | Authenticate and receive JWT |
| `GET` | `/health` | Health check |
| `GET` | `/api/users/{userId}/portfolios/paginated` | List user portfolios |
| `GET` | `/api/portfolios/{portfolioId}/investments` | Investment positions |
| `GET` | `/api/portfolios/{id}/investments/{id}/pnl` | Real-time P&L calculation |
| `POST` | `/api/portfolios/{id}/transactions` | Record buy/sell transaction |
| `GET` | `/api/users/{userId}/alerts/active` | Active price alerts |
| `POST` | `/api/assets/search` | Search assets by ticker |

### API Response Example

**Real-time Investment P&L:**

```json
GET /api/portfolios/1/investments/5/pnl

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
```

---

## 📊 Database & Architecture

### Database Diagram

![Database Model](docs/img.png)

### Layered Architecture

```
├─ Controllers      (REST endpoints, request validation)
├─ Services         (Business logic, calculations, transactions)
├─ Repositories     (Data access layer, queries)
├─ Entities         (Domain models, JPA mappings)
├─ Mappers          (DTO ↔ Entity conversions)
├─ Security         (JWT filters, authentication)
├─ Schedulers       (Automated jobs)
└─ Integration      (External APIs - Brapi)
```

---

## ✅ Project Status

### Implementation Summary

| Category | Progress |
|----------|----------|
| **Core Features (Users, Portfolios, Investments, Transactions)** | ✅ Complete |
| **Real-time Calculations (P&L, average price)** | ✅ Complete |
| **Brapi Integration** | ✅ Complete |
| **Automated Schedulers** | ✅ Complete (price updates, alerts every 5 min) |
| **Security (JWT, BCrypt)** | ✅ Complete |
| **API Documentation (Swagger)** | ✅ Complete |
| **Docker & Database Migrations** | ✅ Complete |
| **Testing Suite** | ⏳ In Progress (unit & integration tests) |
| **Dashboard & Charts** | ⏳ Planned |
| **Role-based Authorization** | ⏳ Planned |
| **Rate Limiting & Observability** | ⏳ Planned |

---

## 🎓 Architecture & Design Highlights

### What This Project Demonstrates

| Competency | Implementation Details |
|------------|------------------------|
| **Layered Architecture** | Clean separation (Controller → Service → Repository layers) |
| **REST API Design** | RESTful endpoints, nested resources, pagination, proper HTTP semantics |
| **Object-Oriented Design** | Inheritance, polymorphism, encapsulation across entities and services |
| **Persistence Layer** | JPA relationships, custom queries, transaction management |
| **External API Integration** | Brapi client with error handling, caching, and fallback strategies |
| **Scheduled Jobs** | Cron-based automation for price updates and alert monitoring |
| **Data Validation** | Jakarta Bean Validation with custom error messages |
| **DTO Mapping** | MapStruct for type-safe entity/DTO conversions |
| **Security** | JWT stateless authentication, BCrypt password hashing, Spring Security filters |
| **Docker Containerization** | Multi-stage builds, Docker Compose orchestration |
| **Database Versioning** | Flyway migrations for reproducible schema evolution |
| **Error Handling** | Global exception handler with standardized API error responses |
| **API Documentation** | OpenAPI 3.0 with Swagger UI for interactive testing |

---

## 🧪 Testing & Quality

**Current Status:** Basic test infrastructure in place. Expanding coverage.

```bash
./mvnw test          # Run all tests
./mvnw test -X       # Verbose mode
```

**Planned Expansion:**
- ✅ Unit tests for Services (business logic)
- ✅ Integration tests for Controllers (REST endpoints)  
- ✅ Repository tests with @DataJpaTest
- 🎯 Target: 80%+ code coverage

---

## 🗺️ Roadmap

| Phase | Objectives | Status |
|-------|-----------|--------|
| **Phase 1** | Core CRUD operations + Data model | ✅ Completed |
| **Phase 2** | Real-time B3 integration + Schedulers | ✅ Completed |
| **Phase 3** | Security & JWT authentication | ✅ Completed |
| **Phase 4** | Comprehensive test suite (80%+ coverage) | ⏳ In Progress |
| **Phase 5** | Dashboard with metrics & performance charts | ⏳ Planned |
| **Phase 6** | Cloud deployment & docs enhancement | ⏳ Planned |

---

##  Author

Built with ☕ by [Davi Dantas](https://github.com/DaviDantass)

This project demonstrates professional-grade Java development practices suitable for portfolio showcasing and production-level applications.
