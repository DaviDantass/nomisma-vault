package com.davidantasdev.nomismavault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
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
                                API REST para gestão de portfólios financeiros.

                                O sistema permite cadastro de usuários, autenticação via JWT,
                                gestão de carteiras, ativos, categorias e transações de compra/venda.

                                A partir das transações registradas, a API acompanha posições,
                                calcula indicadores do portfólio e integra dados de mercado via BRAPI
                                para consulta de cotações e análise de desempenho.

                                ## Funcionalidades principais

                                - **Autenticação**: Login com geração de token JWT
                                - **Carteiras**: Gestão de portfólios de investimento
                                - **Ativos**: Cadastro e consulta de ativos financeiros
                                - **Transações**: Registro de compras e vendas
                                - **Posições**: Acompanhamento de ativos em carteira
                                - **Categorias**: Organização dos investimentos
                                - **Cotações**: Integração com dados de mercado via BRAPI
                                - **Alertas de preço**: Monitoramento de ativos com valores alvo
                                """)
                        .contact(new Contact()
                                .name("Davi Dantas")
                                .url("https://github.com/davidantass")
                                .email("davidantasdev@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Ambiente local")
                ));
    }
}