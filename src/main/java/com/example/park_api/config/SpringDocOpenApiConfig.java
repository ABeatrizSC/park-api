package com.example.park_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration indica que esta classe contém definições de beans que serão gerenciadas pelo Spring.
@Configuration
public class SpringDocOpenApiConfig {

    // O metodo openAPI é anotado com @Bean, o que significa que o retorno deste metodo será um bean gerenciado pelo Spring.
    // Ele configura o OpenAPI (especificação do Swagger 3.0) para documentação da API.
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // Define os componentes da API, incluindo o esquema de segurança.
                .components(new Components().addSecuritySchemes("security", securityScheme()))

                // Define as informações gerais da API, como título, descrição, versão e detalhes do contato.
                .info(
                        new Info()
                                .title("REST API - Spring Park")
                                .description("API para gestão de estacionamento de veículos")
                                .version("v1")
                                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                                .contact(new Contact().name("Ana Beatriz").email("anabeatrizscarmoni@gmail.com"))
                );
    }

    // Metodo que cria um esquema de segurança baseado em JWT para o OpenAPI.
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .description("Insira um bearer token valido para prosseguir") // Mensagem de ajuda para o usuário
                .type(SecurityScheme.Type.HTTP) // Tipo de esquema de segurança (HTTP)
                .in(SecurityScheme.In.HEADER) // O token será enviado no cabeçalho HTTP
                .scheme("bearer") // Esquema de autenticação é "bearer"
                .bearerFormat("JWT") // O formato do token será JWT
                .name("security"); // Nome dado ao esquema de segurança
    }
}
