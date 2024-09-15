package com.example.park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j // Ativa o logging para a classe
public class JwtUtils {

    // Constantes para o padrão de autorização e a chave secreta do JWT
    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final String SECRET_KEY = "0123456789-0123456789-0123456789";

    // Tempo de expiração do token em dias, horas e minutos
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 2;

    // Construtor privado para impedir a criação de instâncias da classe
    private JwtUtils(){
    }

    // Metodo que gera a chave para assinatura do token JWT usando o algoritmo HMAC e a chave secreta
    private static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Metodo que converte a data inicial em uma data de expiração baseada no tempo configurado
    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    // Metodo que cria um token JWT com base no username e role do usuário
    public static JwtToken createToken(String username, String role) {
        Date issuedAt = new Date(); // Data de emissão do token
        Date limit = toExpireDate(issuedAt); // Data de expiração calculada

        // Construção do token JWT
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT") // Define o tipo de token como JWT
                .setSubject(username) // Define o username como o sujeito do token
                .setIssuedAt(issuedAt) // Define a data de emissão
                .setExpiration(limit) // Define a data de expiração
                .signWith(generateKey(), SignatureAlgorithm.HS256) // Assina o token com a chave gerada
                .claim("role", role) // Adiciona a role como um claim
                .compact(); // Gera o token como uma string compacta

        return new JwtToken(token); // Retorna o token JWT encapsulado em uma classe JwtToken
    }

    // Metodo que extrai os claims do token JWT (informações como sujeito, expiração, etc.)
    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build() // Usa a chave gerada para validar o token
                    .parseClaimsJws(refactorToken(token)).getBody(); // Extrai os claims do token
        } catch (JwtException ex) {
            log.error(String.format("Token invalido %s", ex.getMessage())); // Log de erro em caso de token inválido
        }
        return null; // Retorna null se o token for inválido
    }

    // Metodo que extrai o username do token JWT
    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject(); // Retorna o campo "subject" dos claims, que é o username
    }

    // Metodo que verifica se o token JWT é válido
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build() // Usa a chave para validar o token
                    .parseClaimsJws(refactorToken(token)); // Analisa o token JWT e verifica sua validade
            return true; // Retorna true se o token for válido
        } catch (JwtException ex) {
            log.error(String.format("Token invalido %s", ex.getMessage())); // Log de erro em caso de token inválido
        }
        return false; // Retorna false se o token for inválido
    }

    // Metodo que ajusta o token, removendo o prefixo "Bearer " se presente
    private static String refactorToken(String token) {
        if (token.contains(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length()); // Remove o prefixo "Bearer " do token
        }
        return token; // Retorna o token original se não houver o prefixo
    }
}