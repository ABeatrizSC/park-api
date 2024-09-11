package com.example.park_api.resources.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserCreateDTO {
    private String username;
    private String password;
}
