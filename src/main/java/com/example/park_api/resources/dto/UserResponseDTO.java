package com.example.park_api.resources.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {
    private Long id;
    private String username;
    private String role;
}
