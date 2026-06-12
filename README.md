# NomismaVault

API REST para controle de carteira de investimentos. O projeto cobre cadastro de usuarios, autenticacao JWT, catalogo de ativos, carteiras, posicoes, transacoes de compra/venda, calculo de P&L e alertas simples de preco.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security
- JWT 
- PostgreSQL, Flyway e h2 (testes)
- MapStruct
- Maven
- Docker Compose
- Swagger/OpenAPI

## Funcionalidades

- Cadastro e login de usuarios com senha criptografada via BCrypt.
- Autenticacao stateless com JWT.
- Registro de transacoes de compra e venda.
- Posicoes de investimento derivadas exclusivamente das transacoes.
- Atualizacao automatica da posicao ao registrar compra ou venda.
- Fechamento automatico da posicao quando a venda zera a quantidade.
- CRUD de categorias de investimento.
- CRUD de ativos vinculados a categorias.
- CRUD de carteiras por usuario.
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
| `BRAPI_API_TOKEN` | vazio | |

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

## Observacoes tecnicas

- Posicoes (`Investment`) sao derivadas de transacoes. A API permite consultar posicoes e P&L, mas nao permite criar, editar ou excluir posicoes diretamente.
- Uma venda que zera a quantidade remove a posicao aberta da carteira, mantendo a transacao no historico.
- Excluir uma transacao nao recalcula a posicao. Para um produto real, o ideal seria implementar recalculo por historico ou bloquear delecao de transacoes liquidadas.
- Os schedulers de preco/alerta sao aceitaveis no monolito atual, mas nao devem virar fila ou mensageria enquanto o projeto for pequeno.
- O README antigo prometia cobertura e recursos alem do que estava validado. Esta versao documenta o comportamento que o projeto realmente entrega.
