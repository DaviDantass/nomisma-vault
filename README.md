<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</p>

<h1 align="center">💰 NomismaVault</h1>

<p align="center">
  <strong>API REST para gestão de carteiras de investimentos e acompanhamento de ativos da B3.</strong>
</p>

<p align="center">
  <a href="#-sobre">Sobre</a> •
  <a href="#-recursos">Recursos</a> •
  <a href="#-tecnologias">Tecnologias</a> •
  <a href="#-como-executar">Como executar</a> •
  <a href="#-testes">Testes</a>
</p>

---

## 📋 Sobre

O **NomismaVault** é um backend para controle de investimentos pessoais. A aplicação permite organizar múltiplas carteiras, registrar compras e vendas, acompanhar posições e calcular rentabilidade com cotações obtidas pela integração com a [Brapi](https://brapi.dev/).

O projeto demonstra uma API completa com regras de negócio, autenticação, persistência, integração externa, tarefas agendadas e ambiente conteinerizado.

> *Nomisma* (νόμισμα) significa “moeda” em grego antigo e está na origem da palavra **numismática**.

## ✨ Recursos

- Cadastro e autenticação de usuários com JWT e BCrypt
- Múltiplas carteiras isoladas por usuário
- Registro de compras e vendas com taxas
- Atualização automática de quantidade e preço médio
- Cálculo de valor investido, valor de mercado e lucro ou prejuízo
- Catálogo de ativos por categoria e nível de risco
- Cotações de mercado por integração com a Brapi
- Histórico de preços e alertas condicionais
- Rotinas agendadas para atualização de preços e monitoramento de alertas
- Paginação, ordenação e filtros por período
- Respostas de erro padronizadas
- Documentação interativa com Swagger/OpenAPI

## 🛠️ Tecnologias

| Área | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Segurança | Spring Security, JWT e BCrypt |
| Persistência | Spring Data JPA e Hibernate |
| Banco de dados | PostgreSQL 15 e H2 para testes locais |
| Migrações | Flyway |
| Mapeamento | MapStruct |
| Documentação | SpringDoc OpenAPI / Swagger UI |
| Testes | JUnit 5 e Mockito |
| Infraestrutura | Docker e Docker Compose |

## 🚀 Como executar

### Com Docker

Pré-requisito: Docker com Docker Compose.

```bash
git clone https://github.com/davidantasdev/nomisma-vault.git
cd nomisma-vault
docker-compose up -d
```

Após a inicialização:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/health`

Para consultar cotações que exigem autenticação na Brapi, defina `BRAPI_API_TOKEN` no ambiente antes de iniciar os containers.

### Com banco H2

Pré-requisitos: Java 21 e Maven 3.9+.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Nesse modo, a API utiliza um banco em memória e fica disponível em `http://localhost:8082`.

## 📚 Documentação da API

Com a aplicação em execução, o Swagger UI apresenta os contratos e permite testar os endpoints de:

- autenticação e usuários;
- carteiras, investimentos e transações;
- ativos e categorias;
- alertas e histórico de preços.

Acesse `http://localhost:8080/swagger-ui.html` no ambiente Docker ou `http://localhost:8082/swagger-ui.html` no ambiente local.

## 🧪 Testes

Execute a suíte automatizada com:

```bash
mvn test
```

Os testes cobrem inicialização do contexto, usuários, carteiras, investimentos, transações e alertas de preço.

## 🏗️ Arquitetura

```text
controller  → endpoints REST
service     → regras de negócio
repository  → acesso a dados
entity      → modelo de persistência
dto         → contratos de entrada e saída
mapper      → conversão entre entidades e DTOs
security    → autenticação e autorização
integration → comunicação com a Brapi
scheduler   → atualização de preços e alertas
```

## 📄 Licença

Distribuído sob a licença MIT. Consulte [LICENSE](LICENSE) para mais informações.
