
<div align="center">

# 📈 AssetAPI

### API para gerenciamento de investimentos

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge\&logo=spring-boot\&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge\&logo=postgresql\&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge\&logo=docker\&logoColor=white)](https://www.docker.com/)

</div>

---

## Sobre o Projeto

**AssetAPI** é uma API REST para gerenciamento de investimentos. 
### 🎯 Objetivo

Criar uma plataforma para:

* Gerenciar múltiplas carteiras de investimentos
* Calcular rentabilidade e diversificação
* Integrar com APIs de cotação (Brapi)
* Gerar relatórios de performance
* Rastrear transações e evolução patrimonial

> **Status**: Projeto em desenvolvimento - Features estão sendo implementadas incrementalmente

---

## Stack

* **Java 21** - Linguagem principal
* **Spring Boot 4.0.1** - Framework web
* **Spring Data JPA** - Camada de persistência
* **Spring Validation** - Validações de entrada
* **Spring Cache** - Cache de dados
* **PostgreSQL 15** - Banco de dados relacional
* **Flyway** - Versionamento de schema
* **Lombok 1.18.30** - Redução de boilerplate
* **MapStruct 1.5.5** - Mapeamento de DTOs
* **Docker & Docker Compose** - Containerização
* **Maven** - Build e dependências
* **Brapi API** - Cotações de ações brasileiras (B3)

---

## Arquitetura

### 📁 Estrutura do Projeto (Até o momento)

```
src/main/java/com/davidantasdev/AssetAPI/
│
├── controller/           # Endpoints REST
│   ├── HealthController
│   ├── UserController
│   └── PortfolioController
│
├── service/              # Lógica de negócio
│   ├── UserService
│   └── PortfolioService
│
├── repository/           # Acesso a dados (JPA)
│   ├── UserRepository
│   └── PortfolioRepository
│
├── entity/              # Entidades do banco
│   ├── User, Portfolio, Investment
│   ├── Asset, Transaction
│   ├── PriceHistory, PriceAlert
│   └── PortfolioSnapshot
│
├── dto/                 # Data Transfer Objects
│   ├── request/
│   ├── response/
│   └── integration/
│
├── mapper/              # MapStruct mappers
│   ├── UserMapper
│   └── PortfolioMapper
│
├── integration/         # Clientes externos
│   └── BrapiClient
│
├── exception/           # Tratamento de erros
│   ├── GlobalExceptionHandler
│   ├── ResourceNotFoundException
│   └── BusinessException
│
└── config/              # Configurações
    └── RestTemplateConfig
```

### 🔄 Fluxo da Aplicação

```
Cliente (Postman/Browser)
        │
        │ HTTP Request
        ▼
    Controller (REST)
        │
        ▼
    Service (Lógica de Negócio)
        │
        ▼
    Repository (JPA)
        │
        ▼
    PostgreSQL Database
```

---

## Modelo de Dados

### Diagrama ER

```
┌─────────────┐         ┌─────────────────────┐         ┌──────────────┐
│    Users    │         │   Investment        │         │    Assets    │
│─────────────│         │   Categories        │         │──────────────│
│ id (PK)     │         │─────────────────────│         │ id (PK)      │
│ name        │         │ id (PK)             │         │ ticker       │
│ email       │         │ name                │    ┌────│ name         │
│ password    │         │ description         │    │    │ category_id  │
│ created_at  │         │ risk_level          │    │    │ current_price│
└─────────────┘         └─────────────────────┘    │    │ last_update  │
       │                                           │    └──────────────┘
       │                                           │           │
       │  1:N            1:N                   N:1 │           │
       │          ┌─────────────┐                  │           │
       └─────────>│ Portfolios  │                  │           │
                  │─────────────│                  │           │
                  │ id (PK)     │                  │           │
                  │ user_id (FK)│                  │           │
                  │ name        │                  │           │
                  │ description │                  │           │
                  └─────────────┘                  │           │
                        │                          │           │
                        │ 1:N                      │           │
                        │                          │           │
       ┌────────────────┴────────────┬─────────────┘           │
       │                             │                         │
       ▼                             ▼                         │
┌─────────────┐            ┌──────────────┐                    │
│Transactions │            │ Investments  │                    │
│─────────────│            │──────────────│                    │
│ id (PK)     │            │ id (PK)      │                    │
│portfolio_id │            │portfolio_id  │                    │
│ asset_id    │◄───────────│ asset_id     │◄───────────────────┘
│ type        │            │ quantity     │
│ quantity    │            │average_price │
│ price       │            │purchase_date │
│ total_amount│            └──────────────┘
│ fees        │                    │
│transaction_ │                    │ 1:N
│    date     │                    ▼
└─────────────┘           ┌──────────────────┐
                          │Portfolio         │
                          │Snapshots         │
                          │──────────────────│
                          │ id (PK)          │
                          │ portfolio_id (FK)│
                          │ total_invested   │
                          │ current_value    │
                          │ profit_loss      │
                          │ profit_loss_%    │
                          │ snapshot_date    │
                          └──────────────────┘

┌─────────────┐            ┌──────────────┐
│Price Alerts │            │Price History │
│─────────────│            │──────────────│
│ id (PK)     │            │ id (PK)      │
│ user_id (FK)│            │ asset_id (FK)│
│ asset_id    │            │ price        │
│target_price │            │ date         │
│ condition   │            └──────────────┘
│ is_active   │
│ triggered_at│
└─────────────┘
```

### Categorias de Investimento

| Categoria        | Risco    | Exemplos                      |
| ---------------- | -------- | ----------------------------- |
| **Ações**        | 🔴 Alto  | PETR4, VALE3, ITUB4           |
| **FIIs**         | 🟡 Médio | HGLG11, KNRI11, VISC11        |
| **Renda Fixa**   | 🟢 Baixo | Tesouro Direto, CDB, LCI      |
| **Criptomoedas** | 🔴 Alto  | Bitcoin, Ethereum             |
| **Fundos**       | 🟡 Médio | Fundos de Ações, Multimercado |

---

## Roadmap

**Fase 1 [ x ]– Fundação (Concluído)**
Setup do projeto, modelagem do banco, Docker, JPA, controllers básicos, services, exception handling e integração com Brapi.

**Fase 2 [  ]– Core Features (Em Andamento)**
Investimentos e transações, cálculos de performance, dashboard, autenticação JWT e validações.

**Fase 3 [  ]– Avançado (Planejado)**
Atualização automática de preços, alertas, snapshots de portfólio, gráficos, relatórios, testes e documentação.

---

## Desenvolvimento

O projeto já está containerizado com Docker, estruturado em camadas (Controller, Service, Repository), com tratamento global de erros, integração com a API Brapi e mapeamento de DTOs via MapStruct.

Atualmente, estão em desenvolvimento todas as operações de persistência e manipulação de dados para cada entidade, incluindo suas lógicas específicas, autenticação e autorização, cálculos financeiros, endpoints do dashboard e testes automatizados.

---

## **Davi Dantas [@davidantasdev](https://github.com/davidantass)**

---

<div align="center">

</div>  



