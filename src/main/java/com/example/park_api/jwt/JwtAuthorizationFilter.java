package com.example.park_api.jwt;

// Importações de classes essenciais para captura e autenticação de requisições
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService userDetailsService;

    // Metodo que intercepta todas as requisições HTTP e verifica a presença de um token JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Recupera o token do cabeçalho da requisição (Header 'Authorization')
        final String token = request.getHeader(JwtUtils.JWT_AUTHORIZATION);

        if (token == null || !token.startsWith(JwtUtils.JWT_BEARER)) {
            log.info("JWT Token está nulo, vazio ou não iniciado com 'Bearer '.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!JwtUtils.isTokenValid(token)) {
            log.warn("JWT Token está inválido ou expirado.");
            filterChain.doFilter(request, response);
            return;
        }

        String username = JwtUtils.getUsernameFromToken(token);

        // Autentica o usuário com base no username
        toAuthentication(request, username);

        filterChain.doFilter(request, response);
    }

    // Metodo responsável por autenticar o usuário com base no username
    private void toAuthentication(HttpServletRequest request, String username) {
        // Carrega os detalhes do usuário a partir do username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Cria um token de autenticação com os detalhes do usuário e suas autoridades (roles/permissões)
        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());

        // Define os detalhes da requisição para o token de autenticação
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Armazena o token de autenticação no contexto de segurança do Spring Security
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
