# NomismaVault

API REST monolitica para controle de carteira de investimentos. O projeto cobre cadastro de usuarios, autenticacao JWT, catalogo de ativos, carteiras, posicoes, transacoes de compra/venda, calculo de P&L e alertas simples de preco.

O objetivo do repositorio e demonstrar uma API Spring Boot pequena, organizada por camadas e executavel localmente.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security
- JWT com `java-jwt`
- PostgreSQL + Flyway no perfil padrao
- H2 no perfil `local` e nos testes
- MapStruct
- Maven
- Docker Compose
- Swagger/OpenAPI

## Funcionalidades

- Cadastro e login de usuarios com senha criptografada via BCrypt.
- Autenticacao stateless com JWT.
- CRUD de categorias de investimento.
- CRUD de ativos vinculados a categorias.
- CRUD de carteiras por usuario.
- Registro de transacoes de compra e venda.
- Posicoes de investimento derivadas exclusivamente das transacoes.
- Atualizacao automatica da posicao ao registrar compra ou venda.
- Fechamento automatico da posicao quando a venda zera a quantidade.
- Bloqueio de venda acima da quantidade disponivel.
- Resumo financeiro da carteira com total investido, valor atual e lucro/prejuizo.
- Listagem e filtro de transacoes por periodo.
- Calculo de P&L de uma posicao usando cotacao externa da BRAPI.
- Alertas de preco e schedulers simples para monitoramento.

## Arquitetura

O projeto e um monolito Spring Boot em camadas:

```text
controller  -> entrada HTTP e validacao de requests
service     -> regras de negocio e transacoes
repository  -> acesso a dados com Spring Data JPA
entity      -> modelo persistido
dto         -> contratos de entrada e saida
mapper      -> conversao DTO/entity com MapStruct
security    -> JWT, filtro e configuracao do Spring Security
integration -> cliente BRAPI
scheduler   -> jobs de precos e alertas
```

Essa estrutura faz sentido para o tamanho do projeto. Nao ha necessidade de microsservicos, filas, Redis ou mensageria para o escopo atual.

## Como rodar localmente

### Opcao rapida com H2 em memoria

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

No Windows:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

A API fica em:

- `http://localhost:8080`
- Health: `http://localhost:8080/health`
- Swagger: `http://localhost:8080/swagger-ui.html`

Observacao: o perfil `local` usa H2 em memoria e recria o schema ao iniciar. Use esse perfil para testar a API sem instalar PostgreSQL.

### Com PostgreSQL via Docker Compose

```bash
docker compose up --build
```

O Compose sobe PostgreSQL e a API. As migrations Flyway rodam no startup.

## Variaveis de ambiente

| Variavel | Default | Uso |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/nomismavault` | URL do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuario do banco |
| `SPRING_DATASOURCE_PASSWORD` | `manager` | Senha do banco |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `validate` | Validacao/criacao de schema |
| `SPRING_FLYWAY_ENABLED` | `true` | Liga/desliga migrations |
| `JWT_SECRET` | valor fake de dev | Chave de assinatura JWT |
| `JWT_EXPIRATION` | `86400000` | Expiracao configurada, em ms |
| `BRAPI_API_URL` | `https://brapi.dev/api` | URL base da BRAPI |
| `BRAPI_API_TOKEN` | vazio | Token da BRAPI, se necessario |

Nao use os defaults em producao.

## Como testar

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Os testes usam perfil `test` com H2 em memoria. A suite cobre:

- carga do contexto Spring;
- criptografia de senha no cadastro;
- compra criando/atualizando posicao;
- venda reduzindo posicao;
- venda total encerrando posicao aberta;
- erro esperado ao vender mais do que a quantidade disponivel;
- resumo financeiro de carteira;
- calculo de P&L de investimento com cotacao mockada.

## Deploy no Render

O projeto esta pronto para deploy no Render via Docker. O arquivo `render.yaml` define o servico web, health check em `/health` e variaveis esperadas.

Passos:

1. Crie um PostgreSQL no Render.
2. Crie um Web Service a partir deste repositorio usando Docker.
3. Configure as variaveis abaixo no servico web.

Variaveis obrigatorias no Render:

| Variavel | Observacao |
| --- | --- |
| `SPRING_DATASOURCE_URL` | Use formato JDBC: `jdbc:postgresql://host:5432/database` |
| `SPRING_DATASOURCE_USERNAME` | Usuario do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave longa e privada |

Variaveis opcionais:

| Variavel | Valor recomendado |
| --- | --- |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `validate` |
| `SPRING_FLYWAY_ENABLED` | `true` |
| `SPRING_JPA_SHOW_SQL` | `false` |
| `APP_LOG_LEVEL` | `INFO` |
| `BRAPI_API_TOKEN` | Token da BRAPI, se necessario |

O Render injeta a variavel `PORT`; a aplicacao ja usa `server.port=${PORT:8080}`.

## Exemplos de endpoints

### Health

```http
GET /health
```

### Cadastro

```http
POST /auth/register
Content-Type: application/json

{
  "name": "Davi",
  "email": "davi@example.com",
  "password": "123456"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "davi@example.com",
  "password": "123456"
}
```

Use o token retornado nos demais endpoints:

```http
Authorization: Bearer <token>
```

### Criar categoria

```http
POST /categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "ACOES",
  "description": "Acoes listadas em bolsa",
  "riskLevel": "HIGH"
}
```

### Criar ativo

```http
POST /assets
Authorization: Bearer <token>
Content-Type: application/json

{
  "ticker": "PETR4",
  "name": "Petrobras PN",
  "categoryId": 1,
  "currentPrice": 35.50
}
```

### Criar carteira

```http
POST /users/1/portfolios
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Carteira principal",
  "description": "Investimentos de longo prazo"
}
```

### Registrar compra

```http
POST /portfolios/1/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "portfolioId": 1,
  "assetId": 1,
  "type": "BUY",
  "quantity": 10,
  "price": 20.00,
  "transactionDate": "2026-06-11",
  "fees": 1.50,
  "notes": "Compra inicial"
}
```

### Registrar venda

```http
POST /portfolios/1/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "portfolioId": 1,
  "assetId": 1,
  "type": "SELL",
  "quantity": 2,
  "price": 25.00,
  "transactionDate": "2026-06-11"
}
```

### Consultar P&L

```http
GET /portfolios/1/investments/1/pnl
Authorization: Bearer <token>
```

Esse endpoint consulta cotacao na BRAPI. Sem internet ou sem dado para o ticker, a API retorna erro de negocio.

### Consultar resumo da carteira

```http
GET /users/1/portfolios/1/summary
Authorization: Bearer <token>
```

Resposta esperada:

```json
{
  "portfolioId": 1,
  "portfolioName": "Carteira principal",
  "positionsCount": 2,
  "totalInvested": 400.00,
  "currentValue": 450.00,
  "profitLoss": 50.00,
  "profitLossPercent": 12.5000
}
```

O resumo usa o `currentPrice` salvo no ativo. Quando o ativo ainda nao tem preco atual, usa o preco medio da posicao como fallback.

## Observacoes tecnicas

- Posicoes (`Investment`) sao derivadas de transacoes. A API permite consultar posicoes e P&L, mas nao permite criar, editar ou excluir posicoes diretamente.
- Uma venda que zera a quantidade remove a posicao aberta da carteira, mantendo a transacao no historico.
- Excluir uma transacao nao recalcula a posicao. Para um produto real, o ideal seria implementar recalculo por historico ou bloquear delecao de transacoes liquidadas.
- Os schedulers de preco/alerta sao aceitaveis no monolito atual, mas nao devem virar fila ou mensageria enquanto o projeto for pequeno.
- O README antigo prometia cobertura e recursos alem do que estava validado. Esta versao documenta o comportamento que o projeto realmente entrega.
