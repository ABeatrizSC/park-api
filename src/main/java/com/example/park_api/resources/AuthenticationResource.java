package com.example.park_api.resources;

import com.example.park_api.jwt.JwtToken;
import com.example.park_api.jwt.JwtUserDetailsService;
import com.example.park_api.resources.dto.UserLoginDTO;
import com.example.park_api.resources.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationResource {
    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;

    // Mapeia a requisição POST para o endpoint "/auth" para realizar a autenticação
    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@RequestBody @Valid UserLoginDTO userLoginDTO, HttpServletRequest request) {
        log.info("Processo de autenticação pelo login {}", userLoginDTO.getUsername());
        try {
            // Cria um token de autenticação a partir do username e senha do DTO
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());

            // Realiza a autenticação utilizando o AuthenticationManager
            authenticationManager.authenticate(authenticationToken);

            // Gera o token JWT para o usuário autenticado com base no username
            JwtToken token = detailsService.getTokenAuthenticated(userLoginDTO.getUsername());

            // Retorna o token em caso de sucesso
            return ResponseEntity.ok(token);
        } catch (AuthenticationException ex) {
            log.warn("Bad Credentials from username '{}'", userLoginDTO.getUsername());
        }
        // Retorna uma resposta de erro caso a autenticação falhe, com uma mensagem personalizada
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais Inválidas"));
    }
}
