package com.example.park_api.resources.dto.mapper;

import com.example.park_api.entities.Cliente;
import com.example.park_api.resources.dto.ClienteCreateDTO;
import com.example.park_api.resources.dto.ClienteResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {

    // Este metodo converte um objeto ClienteCreateDTO em um objeto Cliente.
    // Ele usa a biblioteca ModelMapper para mapear os atributos do DTO para a entidade Cliente.
    public static Cliente toCliente(ClienteCreateDTO dto) {
        return new ModelMapper().map(dto, Cliente.class);
    }

    // Este metodo converte um objeto Cliente em um objeto ClienteResponseDTO.
    // Ele também usa o ModelMapper para realizar o mapeamento dos atributos da entidade para o DTO de resposta.
    public static ClienteResponseDTO toDto(Cliente cliente) {
        return new ModelMapper().map(cliente, ClienteResponseDTO.class);
    }
}
