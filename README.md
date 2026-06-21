<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge" alt="Status"/>
</p>

<h1 align="center">💰 NomismaVault</h1>

<p align="center">
  <strong>REST API for financial portfolio management with B3 market data integration</strong>
</p>

<p align="center">
  <a href="#-overview">Overview</a> •
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-api-documentation">API</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-status">Status</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

---

## 📋 Overview

**NomismaVault** is a RESTful API for personal financial asset tracking, portfolio management, and portfolio performance monitoring.

The project was built with a focus on clean architecture, security, and maintainable backend development practices.

> *"Nomisma"* (νόμισμα) means "coin" in ancient Greek — the etymological origin of **numismatics**.

### 🎯 Core Capabilities

- Multi-portfolio management with isolated user contexts
- P&L calculation through Brapi integration
- B3 market quote tracking
- Automated schedulers for price updates and alerts
- JWT-based authentication with BCrypt password encryption
- Financial calculations such as average price, profitability, and position tracking
- Docker-ready environment with PostgreSQL
- API documentation with Swagger/OpenAPI

---

## ✨ Features

### Implemented ✅

| Feature | Details |
|---|---|
| **User Management** | User registration, validation, and secure authentication |
| **Authentication** | JWT tokens, stateless sessions, and Spring Security configuration |
| **Portfolios** | Multiple portfolios per user with pagination and sorting |
| **Investments** | Position tracking, real-time P&L, and automatic average price calculation |
| **Transactions** | Buy/sell history, date filtering, and fee tracking |
| **Price Alerts** | Conditional alerts using ABOVE/BELOW rules |
| **Asset Catalog** | Stocks, REITs, crypto, fixed income, and risk categorization |
| **Brapi Integration** | Live market quotes, caching, and error handling |
| **Schedulers** | Automated price updates and alert monitoring |
| **Global Error Handling** | Standardized API error responses |
| **Database Migrations** | Versioned schema management with Flyway |

### Planned ⏳

- Dashboard with aggregated portfolio metrics
- Category diversification charts
- Performance comparison with CDI/IPCA benchmarks
- Comprehensive test suite with higher coverage
- Role-based authorization
- Rate limiting and observability

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|---|---|---|
| **Runtime** | Java / Eclipse Temurin | 21 LTS |
| **Framework** | Spring Boot | 4.0.1 |
| **ORM** | Spring Data JPA + Hibernate | - |
| **Database** | PostgreSQL | 15 |
| **Validation** | Jakarta Bean Validation | - |
| **Mapping** | MapStruct | 1.5.5 |
| **Security** | Spring Security + JWT/Auth0 | 4.2.1 |
| **API Docs** | SpringDoc OpenAPI / Swagger UI | 2.8.5 |
| **Cache** | Spring Cache | - |
| **DB Migrations** | Flyway | - |
| **Container** | Docker + Docker Compose | - |
| **Build Tool** | Maven | 3.9+ |

---

## 🚀 Getting Started

### Prerequisites

You can run the project using Docker or manually with Java and PostgreSQL.

- Docker and Docker Compose

Or:

- Java 21
- Maven 3.9+
- PostgreSQL 15

### Quick Start with Docker

```bash
git clone https://github.com/davidantasdev/nomisma-vault.git
cd nomisma-vault
docker-compose up -d
```

The API will be available at:

- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/health`

### Manual Setup with PostgreSQL

Create the database:

```bash
createdb -U postgres nomismavault
```

Run the application:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/nomismavault"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
mvnw.cmd spring-boot:run
```

Flyway will create the database tables automatically on startup.

If port `8080` is already in use:

```powershell
mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### Local H2 Profile

For a quick run without PostgreSQL:

```powershell
mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

---

## 📚 API Documentation

After starting the application, access:

```text
http://localhost:8080/swagger-ui.html
```

Main endpoint groups:

- Authentication
- Users
- Portfolios
- Assets
- Investment categories
- Investments
- Transactions
- Price alerts
- Price history

---

## 🧪 Tests

Run the automated tests:

```bash
mvnw.cmd test
```

Current test suite covers authentication, password encryption, transaction rules, portfolio summary, P&L calculation, and Spring context loading.

---

## 🏗️ Architecture

```text
controller  -> REST endpoints
service     -> business rules
repository  -> database access
entity      -> persistence model
dto         -> request/response contracts
mapper      -> entity/DTO conversion
security    -> JWT and Spring Security
integration -> external Brapi client
scheduler   -> price and alert routines
```

---

## 📌 Status

The project is currently in development, but the main backend flow is functional and runnable locally with PostgreSQL or H2.

Validated locally:

- PostgreSQL connection
- Flyway migrations
- Swagger UI
- Health check
- Automated tests

---

## 🗺️ Roadmap

- Add more integration tests
- Improve portfolio analytics
- Add benchmark comparison
- Add role-based authorization
- Add observability and request logging
