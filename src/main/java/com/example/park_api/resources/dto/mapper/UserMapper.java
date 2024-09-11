package com.example.park_api.resources.dto.mapper;

import com.example.park_api.entities.User;
import com.example.park_api.resources.dto.UserCreateDTO;
import com.example.park_api.resources.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUser(UserCreateDTO userCreateDTO) {
        return new ModelMapper().map(userCreateDTO, User.class);
    }

    public static UserResponseDTO toDto(User usuario) {
        // Extrai o papel (role) do usuário, removendo o prefixo "ROLE_"
        String role = usuario.getRole().name().substring("ROLE_".length());
        // Cria um PropertyMap para definir uma regra de mapeamento customizada entre User e UserResponseDTO
        PropertyMap<User, UserResponseDTO> props = new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                // Define a configuração de mapeamento para o campo role no UserResponseDTO
                map().setRole(role);
            }
        };
        ModelMapper mapper = new ModelMapper();
        // Adiciona o mapeamento customizado (props) ao ModelMapper
        mapper.addMappings(props);
        // Retorna o objeto UserResponseDTO mapeado a partir do objeto User
        return mapper.map(usuario, UserResponseDTO.class);
    }

    public static List<UserResponseDTO> toListDto(List<User> users) {
        return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
    }
}
