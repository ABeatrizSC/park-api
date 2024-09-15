package com.example.park_api.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private com.example.park_api.entities.User usuario;

    public JwtUserDetails(com.example.park_api.entities.User usuario) {
        // Chama o construtor da classe `User` do Spring Security, passando o username, password,
        // e as roles do usuário como uma lista de autoridades/roles
        super(usuario.getUsername(), usuario.getPassword(), AuthorityUtils.createAuthorityList(usuario.getRole().name()));
        this.usuario = usuario;
    }

    public Long getId() {
        return this.usuario.getId();
    }

    public String getRole() {
        return this.usuario.getRole().name();
    }
}
