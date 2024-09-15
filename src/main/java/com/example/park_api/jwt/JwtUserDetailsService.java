package com.example.park_api.jwt;

import com.example.park_api.entities.User;
import com.example.park_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    // Implementa o metodo `loadUserByUsername`, obrigatório pela interface `UserDetailsService`
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuario = userService.buscarPorUsername(username);
        // Retorna uma instância de `JwtUserDetails`, que é uma representação do usuário para o Spring Security
        return new JwtUserDetails(usuario);
    }

    public JwtToken getTokenAuthenticated(String username) {
        User.Role role = userService.buscarRolePorUsername(username);
        // Cria e retorna um token JWT, removendo o prefixo "ROLE_" da role do usuário
        return JwtUtils.createToken(username, role.name().substring("ROLE_".length()));
    }
}
