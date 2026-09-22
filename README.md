<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-PostgreSQL-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

<h1 align="center">NomismaVault</h1>

<p align="center">
  <strong>API REST para gestão de investimentos pessoais, carteiras e dados de mercado.</strong>
</p>

<p align="center">
  <a href="#-sobre">Sobre</a> •
  <a href="#-recursos">Recursos</a> •
  <a href="#️-tecnologias">Tecnologias</a> •
  <a href="#-como-executar">Como executar</a> •
  <a href="#-testes">Testes</a>
</p>

---

## 📋 Sobre

O **NomismaVault** é um backend para controle de investimentos pessoais. Ele permite organizar carteiras, registrar compras e vendas, acompanhar posições, consolidar patrimônio e calcular P&L sem tornar as operações financeiras dependentes de uma API externa.

As cotações são obtidas pela [Brapi](https://brapi.dev/) por uma fronteira interna com cache, timeout e fallback. SELIC e IPCA são consultados no Banco Central. Cada recurso financeiro pertence ao usuário autenticado por JWT.

> *Nomisma* (νόμισμα) significa “moeda” em grego antigo e está na origem da palavra **numismática**.

## ✨ Recursos

- Cadastro, login e perfil próprio com JWT e BCrypt.
- Carteiras, ativos, categorias, compras e vendas com taxas.
- Atualização transacional de quantidade e preço médio, com bloqueio de venda acima do saldo.
- Dashboard em `/me/dashboard` com patrimônio, P&L, carteiras e alertas ativos.
- Alertas de preço e snapshots diários de patrimônio e de cotações observadas.
- Isolamento de dados por usuário, inclusive nas rotas legadas com `userId`.
- Paginação com contrato estável e respostas de erro padronizadas.
- Cotações Brapi com cache, timeout, fallback e tratamento de indisponibilidade, limite e ativo não encontrado.
- Indicadores SELIC e IPCA pelo Banco Central (SGS).
- Auditoria de criação e exclusão de transações financeiras.
- Swagger/OpenAPI, coleção Postman, CI e migrations Flyway.

## 🛠️ Tecnologias

| Área              | Tecnologia |
|-------------------| --- |
| Linguagem         | Java 21 |
| Framework         | Spring Boot 4 |
| Segurança         | Spring Security, JWT e BCrypt |
| Persistência      | Spring Data JPA, Hibernate e PostgreSQL 15 |
| Migrações         | Flyway |
| Integrações (api) | Brapi e Banco Central (SGS) |
| Cache             | Caffeine |
| Documentação      | SpringDoc OpenAPI / Swagger UI |
| Testes            | JUnit 5, Mockito, MockMvc e Testcontainers |
| Infraestrutura    | Docker Compose e GitHub Actions |

## 🚀 Como executar

### Com PostgreSQL no Docker

Pré-requisitos: Docker Desktop e Java 21. O Compose sobe somente o banco; a API é executada localmente pela Maven Wrapper.

```bash
git clone https://github.com/davidantasdev/nomisma-vault.git
cd nomisma-vault
docker compose up -d
```

Defina no arquivo `.env` as credenciais usadas pelo Compose:

```properties
POSTGRES_DB=nomismavault
POSTGRES_USER=nomismavault_user
POSTGRES_PASSWORD=sua_senha_local
```

Com o banco `healthy`, inicie a API:

```powershell
.\mvnw.cmd spring-boot:run
```

Após a inicialização:

- API: `http://localhost:8082`
- Swagger UI: `http://localhost:8082/swagger-ui.html`
- Health check: `http://localhost:8082/health`

Em produção, configure `SPRING_PROFILES_ACTIVE=prod` e um `JWT_SECRET` com pelo menos 32 caracteres. O projeto rejeita o segredo padrão nesse perfil.

### Com H2 em memória

Para executar sem Docker ou PostgreSQL:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Nesse modo, a API usa H2 em memória e continua disponível em `http://localhost:8082`.

## 📚 Documentação da API

Com a aplicação em execução, o Swagger UI apresenta os contratos e permite testar autenticação, perfil, carteiras, investimentos, transações, ativos, categorias, alertas, dashboard e dados de mercado.

O fluxo principal é:

1. `POST /auth/register` e `POST /auth/login`.
2. Configure `Authorization: Bearer <token>`.
3. Crie uma carteira em `POST /me/portfolios`.
4. Cadastre categoria e ativo; registre compras ou vendas em `/portfolios/{portfolioId}/transactions`.
5. Consulte `GET /me/dashboard` e gerencie alertas em `/me/alerts`.

Para uma apresentação guiada, importe [a coleção Postman](postman/NomismaVault-me.postman_collection.json) e siga o [roteiro de demonstração](docs/DEMO.md).

## 🧪 Testes

Execute a suíte automatizada com:

```powershell
.\mvnw.cmd test
```

A suíte cobre autenticação, ownership, compra e venda, saldo insuficiente, exclusão e recálculo de posição, P&L com fallback, alertas, paginação, cache, schedulers e falhas simuladas da Brapi. Nenhum teste normal faz chamadas à Brapi real.

Para validar migrations em PostgreSQL com Testcontainers, em uma máquina cujo cliente Java tenha acesso ao Docker:

```powershell
.\mvnw.cmd -DrunPostgresIT=true -Dtest=PostgreSqlFlywayTest test
```

## 🏗️ Arquitetura

```text
HTTP / Swagger → controllers → services → repositories → PostgreSQL

MarketController → MarketDataService → MarketDataProvider
                                     → BrapiMarketDataProvider → BrapiClient

Schedulers → services / MarketDataService → cache ou provider
```

O domínio financeiro não depende diretamente da Brapi. `Asset.currentPrice` representa a última cotação conhecida e `PriceHistory` representa snapshots observados pelo NomismaVault, não uma série histórica oficial completa.

## 📄 Licença

Distribuído sob a licença MIT. Consulte [LICENSE](LICENSE) para mais informações.
