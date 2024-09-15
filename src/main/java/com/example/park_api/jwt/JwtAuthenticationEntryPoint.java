package com.example.park_api.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // O metodo commence é chamado sempre que uma exceção de autenticação ocorre.
    // Aqui, ele é usado para lidar com solicitações não autenticadas que tentam acessar recursos protegidos.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Registrar uma mensagem de log sempre que a exceção de autenticação ocorrer.
        // O log conterá o status HTTP 401 (não autorizado) e a mensagem da exceção.
        log.info("Http Status 401 {}", authException.getMessage());

        // Adicionar um cabeçalho na resposta HTTP para informar que a autenticação deve ser feita via JWT (Bearer Token).
        response.setHeader("www-authenticate", "Bearer realm='/api/v1/auth'");

        // Enviar a resposta com o status 401 (Não autorizado) para o cliente.
        response.sendError(401);
    }
}
