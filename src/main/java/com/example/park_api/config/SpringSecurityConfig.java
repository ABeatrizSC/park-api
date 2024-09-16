package com.example.park_api.config;

import com.example.park_api.jwt.JwtAuthenticationEntryPoint;
import com.example.park_api.jwt.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableMethodSecurity // Habilita a segurança baseada em métodos (anotações como @PreAuthorize)
@EnableWebMvc // Habilita o suporte ao Spring MVC (controladores REST e afins)
@Configuration // Indica que essa classe é uma configuração do Spring
public class SpringSecurityConfig {
    private static final String[] DOCUMENTATION_OPENAPI = {
            "/docs/index.html",
            "/docs-park.html", "/docs-park/**",
            "/v3/api-docs/**",
            "/swagger-ui-custom.html", "/swagger-ui.html", "/swagger-ui/**",
            "/**.html", "/webjars/**", "/configuration/**", "/swagger-resources/**"
    };

    // Define um bean que configura a cadeia de filtros de segurança
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Desabilita a proteção CSRF (Cross-Site Request Forgery), pois a API é stateless
                .csrf(csrf -> csrf.disable())
                // Desabilita o login baseado em formulários (não utilizado em APIs stateless)
                .formLogin(form -> form.disable())
                // Desabilita a autenticação HTTP Basic
                .httpBasic(basic -> basic.disable())
                // Configura as regras de autorização para as requisições HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permite todas as requisições POST para o endpoint "api/v1/usuarios"
                        .requestMatchers(HttpMethod.POST, "api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/v1/auth").permitAll()
                        .requestMatchers(DOCUMENTATION_OPENAPI).permitAll() //transforma as URIs em acesso publico, para poder acessar o swagger e etc
                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                // Configura a política de gerenciamento de sessão para "stateless" (sem estado)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).addFilterBefore(
                        jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) //sempre que houver uma exceção referente a usuário nao logado, o spring vai ate a classe acima e lança a exceção
                ).build();
        // Constrói e retorna a cadeia de filtros de segurança
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    // Define um bean para codificação de senha usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define um bean para o gerenciador de autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
