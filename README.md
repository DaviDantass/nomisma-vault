
<div align="left">

# 📈 NomismaVault

### API para Gerenciamento de Investimentos

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

## 🔹 Sobre o Projeto

**NomismaVault** é uma API REST para **gerenciamento de investimentos e carteiras de ativos**, construída com foco em escalabilidade, confiabilidade e rastreabilidade das operações financeiras.

### Objetivos principais
- Gerenciar múltiplas carteiras de investimento
- Adicionar, editar e remover ativos/posições em cada carteira
- Calcular rentabilidade real, ROI e diversificação do portfólio
- Integrar com APIs de cotação (ex.: Brapi, CoinGecko)
- Gerar relatórios de performance e evolução patrimonial
- Rastrear transações financeiras detalhadas (histórico de compra/venda)
- Configurar alertas de preço para ativos
- Armazenar snapshots diários do portfólio para análise de evolução

> **Status**: Em desenvolvimento – novas features estão sendo implementadas incrementalmente.

---

## Stack Tecnológica

- **Java 21** – Linguagem principal
- **Spring Boot 4.0.1** – Framework web
- **Spring Data JPA** – Camada de persistência
- **Spring Validation** – Validação de entradas
- **Spring Cache** – Cache de dados
- **PostgreSQL 15** – Banco de dados relacional
- **Flyway** – Versionamento de schema
- **Lombok 1.18.30** – Redução de boilerplate
- **MapStruct 1.5.5** – Mapeamento de DTOs
- **Docker & Docker Compose** – Containerização
- **Maven** – Gerenciamento de build e dependências
- **Brapi API** – Cotações de ações brasileiras (B3)


---


## Arquitetura

### 📁 Estrutura do Projeto (Até o momento)

```
src/main/java/com/davidantasdev/NomismaVault/
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

## 🗓️ Roadmap & Desenvolvimento

**Fase 1 – Fundação ✅ (Concluído)**
- Setup do projeto, Docker, JPA, controllers e services básicos
- Tratamento global de exceções
- Modelagem do banco PostgreSQL e integração inicial com **Brapi**

**Fase 2 – Core Features (Em Andamento)**
-  operações de persistência de Investimentos e Transações
- Cálculos de performance (rentabilidade, ROI, diversificação)
- Dashboard com métricas resumidas e gráficos básicos
- Alertas de preço configuráveis
- Autenticação JWT e validações de entrada

**Fase 3 – Avançado (Depois)**
- Atualização automática de preços (Schedulers)
- Snapshots diários para análise histórica
- Gráficos detalhados e comparativos
- Testes unitários e de integração completos
- Documentação Swagger e JavaDoc
- Observabilidade / logs 
- Mais Validações, exemplo: @Valid
- Paginação

## Funcionalidades Futuras
- Integração com criptomoedas e exchanges
- Dashboard web para visualização de portfólio
- Alertas de performance e notificações de mercado
- Ferramentas de simulação e projeção de investimentos

---

**By: Davi Dantas [@davidantass](https://github.com/davidantass)**

---

<div align="center">

</div>  



