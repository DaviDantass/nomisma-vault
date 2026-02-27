package com.davidantasdev.nomismavault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NomismaVault API")
                        .version("1.0.0")
                        .description("""
                                REST API para rastreamento de ativos financeiros, performance de portfólios
                                e integração com dados de mercado da B3.

                                ## Funcionalidades Principais

                                - **Portfólios**: Gerencie múltiplas carteiras de investimentos
                                - **Investimentos**: Acompanhe posições com cálculo de P&L em tempo real
                                - **Transações**: Histórico completo de compras e vendas
                                - **Alertas de Preço**: Receba notificações quando ativos atingirem valores alvo
                                - **Integração B3**: Cotações atualizadas via Brapi API
                                """)
                        .contact(new Contact()
                                .name("David Antas")
                                .url("https://github.com/davidantasdev")
                                .email("davidantasdev@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.nomismavault.com")
                                .description("Servidor de Produção")))
                .tags(List.of(
                        new Tag().name("Users").description("Gestão de usuários"),
                        new Tag().name("Portfolios").description("Gestão de carteiras de investimentos"),
                        new Tag().name("Investments").description("Posições e cálculo de P&L"),
                        new Tag().name("Transactions").description("Histórico de compra/venda"),
                        new Tag().name("Assets").description("Cadastro de ativos (ações, FIIs, cripto)"),
                        new Tag().name("Price Alerts").description("Alertas de preço"),
                        new Tag().name("Categories").description("Categorias de investimento"),
                        new Tag().name("Price History").description("Histórico de cotações"),
                        new Tag().name("Health").description("Status da aplicação")));
    }
}
