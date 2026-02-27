<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge" alt="Status"/>
</p>

<h1 align="center">💰 NomismaVault</h1>

<p align="center">
  <strong>REST API para rastreamento de ativos financeiros, performance de portfólios e integração com dados de mercado da B3</strong>
</p>

<p align="center">
  <a href="#-sobre">Sobre</a> •
  <a href="#-funcionalidades">Funcionalidades</a> •
  <a href="#%EF%B8%8F-arquitetura">Arquitetura</a> •
  <a href="#-stack-tecnológica">Stack</a> •
  <a href="#-api-documentation">API Docs</a> •
  <a href="#-como-executar">Executar</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

---

## 📋 Sobre

**NomismaVault** é uma API RESTful completa para rastreamento de ativos financeiros pessoais, desenvolvida com as melhores práticas de engenharia de software. O projeto permite acompanhar múltiplos portfólios, registrar transações de compra/venda, monitorar performance (P&L) em tempo real e receber alertas de preço.

> *"Nomisma"* (νόμισμα) significa "moeda" em grego antigo — a origem etimológica do termo "numismática".

### Destaques Técnicos

- **Arquitetura em camadas** bem definida (Controller → Service → Repository)
- **Integração com API externa** (Brapi - dados da B3 em tempo real)
- **Scheduled Jobs** para atualização automática de preços
- **Cálculos financeiros** (rentabilidade, P&L, preço médio)
- **Validações robustas** com Bean Validation
- **Cache** para otimização de chamadas à API externa
- **Migrations versionadas** com Flyway
- **Docker-ready** com multi-stage build

---

## Funcionalidades

### Implementadas 

#### Gestão de Usuários
- CRUD completo de usuários
- Validação de email único
- Relacionamento com portfólios e alertas

#### Portfólios
- Múltiplos portfólios por usuário
- Nome único por usuário
- Listagem paginada com ordenação

#### Investimentos
- Registro de posições (ativo, quantidade, preço médio)
- **Cálculo de P&L em tempo real** via integração Brapi
- Atualização automática de preço médio em compras
- Constraint única (portfólio + ativo)

#### Transações
- Histórico de compras e vendas
- Filtro por período de datas
- Cálculo automático de taxas

#### Ativos (Assets)
- Cadastro de ações, FIIs, cripto, renda fixa
- Busca por ticker
- Categorização com níveis de risco

#### Alertas de Preço
- Definir alertas ABOVE/BELOW
- Verificação automática a cada 5 minutos (horário de mercado)
- Histórico de alertas disparados

#### Integração Brapi
- Cotações em tempo real da B3
- Cache de requisições
- Fallback de erros

#### Schedulers
- **PriceUpdateScheduler**: Atualiza preços diariamente às 18h (pós-fechamento)
- **PriceAlertScheduler**: Verifica alertas a cada 5 min (10h-18h)

---

### Estrutura de Pacotes

```
src/main/java/com/davidantasdev/nomismavault/
├── config/                 # Configurações (RestTemplate, Security, etc)
├── controller/             # REST Controllers
│   ├── AssetController
│   ├── InvestmentController
│   ├── PortfolioController
│   ├── PriceAlertController
│   ├── TransactionController
│   └── UserController
├── dto/
│   ├── integration/        # DTOs da API Brapi
│   ├── request/            # DTOs de entrada
│   └── response/           # DTOs de saída
├── entity/                 # JPA Entities
│   └── enums/              # Enums (TransactionType, RiskLevel, etc)
├── exception/              # Tratamento global de erros
├── integration/            # Clientes de APIs externas
├── mapper/                 # MapStruct Mappers
├── repository/             # Spring Data JPA Repositories
├── scheduler/              # Scheduled Jobs
├── security/               # Configurações de segurança (em desenvolvimento)
└── service/                # Lógica de negócio
```

---

## Stack Tecnológica

| Camada | Tecnologia | Versão |
|--------|------------|--------|
| **Runtime** | Java (Eclipse Temurin) | 21 LTS |
| **Framework** | Spring Boot | 4.0.1 |
| **Persistência** | Spring Data JPA + Hibernate | - |
| **Database** | PostgreSQL | 15 |
| **Migrations** | Flyway | - |
| **Validações** | Bean Validation (Jakarta) | - |
| **Mapeamento** | MapStruct | 1.5.5 |
| **Boilerplate** | Lombok | 1.18.30 |
| **Cache** | Spring Cache (Simple) | - |
| **Documentação** | SpringDoc OpenAPI (Swagger) | 2.8.4 |
| **Container** | Docker + Docker Compose | - |
| **API Externa** | Brapi (B3 Data) | - |

---

## API Documentation

Documentação interativa completa disponível em:

```
http://localhost:8080/swagger-ui.html
```

---

## 🚀 Como Executar

### Pré-requisitos

- Docker & Docker Compose
- Git

### Com Docker (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/davidantasdev/nomisma-vault.git
cd nomisma-vault

# Suba os containers
docker-compose up -d

# A API estará disponível em http://localhost:8080
```

### Desenvolvimento Local

```bash
# Requisitos: Java 21, Maven 3.9+, PostgreSQL 15

# Configure o banco
createdb nomismavault

# Execute
./mvnw spring-boot:run
```

### Variáveis de Ambiente

| Variável | Descrição | Default |
|----------|-----------|---------|
| `SPRING_DATASOURCE_URL` | URL do PostgreSQL | `jdbc:postgresql://localhost:5432/nomismavault` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `manager` |
| `BRAPI_API_URL` | URL da API Brapi | `https://brapi.dev/api` |
| `BRAPI_API_TOKEN` | Token da Brapi (opcional) | - |
| `JWT_SECRET` | Secret para JWT | - |

---

## Modelagem do Banco
![Database Diagram](docs/img.png)

## Exemplo de Response - P&L

```json
// GET /api/portfolios/1/investments/5/pnl

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

## Checklist de Desenvolvimento

### Core Features
- [x] CRUD de Usuários
- [x] CRUD de Portfólios
- [x] CRUD de Investimentos
- [x] CRUD de Transações (BUY/SELL)
- [x] CRUD de Ativos
- [x] CRUD de Categorias de Investimento
- [x] CRUD de Alertas de Preço
- [x] Histórico de Preços

### Integração & Cálculos
- [x] Integração com Brapi (B3)
- [x] Cálculo de P&L (Profit/Loss)
- [x] Cálculo de preço médio
- [x] Cálculo de rentabilidade %
- [x] Cache de cotações

### Automação
- [x] Scheduler de atualização de preços (18h)
- [x] Scheduler de verificação de alertas (5 min)
- [ ] Scheduler de snapshots diários

### Infraestrutura
- [x] Docker multi-stage build
- [x] Docker Compose (PostgreSQL + Backend)
- [x] Flyway migrations
- [x] Global Exception Handler
- [x] Validações com Bean Validation
- [x] Paginação e ordenação

### Segurança
- [x] Estrutura de Security (em progresso)
- [ ] JWT Authentication
- [ ] Role-based Authorization
- [ ] Rate Limiting
- [ ] Password Encryption (BCrypt)

### Qualidade
- [ ] Testes unitários (Services)
- [ ] Testes de integração (Controllers)
- [ ] Testes de repositório
- [ ] Cobertura mínima 80%

### Documentação
- [x] README completo
- [x] Swagger/OpenAPI annotations
- [ ] JavaDoc nos services principais
- [ ] Collection Postman/Insomnia

### Extras (Pós-MVP)
- [ ] Dashboard com métricas agregadas
- [ ] Diversificação por categoria (gráfico)
- [ ] Comparação com CDI/IPCA
- [ ] Export PDF/Excel
- [ ] Notificações por email
- [ ] Versionamento de API (/v1/)
- [ ] Observabilidade (Micrometer/Prometheus)
- [ ] Deploy cloud (Render/Railway)

---

## Roadmap

| Fase | Descrição | Status |
|------|-----------|--------|
| **Fase 1** | CRUD completo + Modelagem | ✅ Concluído |
| **Fase 2** | Integração Brapi + Schedulers | ✅ Concluído |
| **Fase 3** | Segurança (JWT) | 🔄 Em progresso |
| **Fase 4** | Testes automatizados | ⏳ Pendente |
| **Fase 5** | Dashboard + Gráficos | ⏳ Pendente |
| **Fase 6** | Deploy + Documentação | ⏳ Pendente |

---

## O Que Este Projeto Demonstra:

| Competência | Implementação |
|-------------|---------------|
| **Arquitetura** | Camadas bem definidas, separação de responsabilidades |
| **REST API** | Endpoints RESTful, recursos aninhados, paginação |
| **Persistência** | JPA, queries customizadas, relacionamentos |
| **Integração** | Consumo de API externa com tratamento de erros |
| **Automação** | Scheduled jobs para tarefas recorrentes |
| **Validação** | Bean Validation com mensagens customizadas |
| **Mapeamento** | MapStruct para conversão DTO ↔ Entity |
| **Containerização** | Docker multi-stage, compose orquestrado |
| **Versionamento DB** | Migrations Flyway |
| **Error Handling** | GlobalExceptionHandler com responses padronizados |
| **Documentação** | Swagger UI com OpenAPI 3.0 |

---

## Contribuição

Este é um projeto pessoal de aprendizado, mas sugestões são bem-vindas!
Me chame por email quallquer coisa.

---



<p align="center">
  Desenvolvido com ☕ por <a href="https://github.com/DaviDantass">@DaviDantass</a>
</p>
