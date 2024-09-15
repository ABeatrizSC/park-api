package com.example.park_api;

// Importa classes necessárias para manipulação de tokens JWT, DTOs de login de usuário e cabeçalhos HTTP
import com.example.park_api.jwt.JwtToken;
import com.example.park_api.resources.dto.UserLoginDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

public class JwtAuthentication {
    public static Consumer<HttpHeaders> getHeaderAuthorization(WebTestClient client, String username, String password) {
        String token = client
                .post()                             // Realiza uma requisição POST
                .uri("/api/v1/auth")                // Define o URI de autenticação
                .bodyValue(new UserLoginDTO(username, password))  // Envia as credenciais do usuário no corpo da requisição
                .exchange()                         // Envia a requisição para o servidor
                .expectStatus().isOk()              // Verifica se a resposta tem status HTTP 200 (OK)
                .expectBody(JwtToken.class)         // Espera que o corpo da resposta seja do tipo JwtToken
                .returnResult()                     // Obtém o resultado da resposta
                .getResponseBody()                  // Extrai o corpo da resposta (JwtToken)
                .getToken();                        // Extrai o token JWT do objeto JwtToken

        // Retorna um consumer que adiciona o token JWT no cabeçalho de autorização
        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
