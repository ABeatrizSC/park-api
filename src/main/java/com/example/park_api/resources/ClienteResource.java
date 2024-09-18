package com.example.park_api.resources;

import com.example.park_api.entities.Cliente;
import com.example.park_api.jwt.JwtUserDetails;
import com.example.park_api.repositories.projection.ClienteProjection;
import com.example.park_api.resources.dto.ClienteCreateDTO;
import com.example.park_api.resources.dto.ClienteResponseDTO;
import com.example.park_api.resources.dto.PageableDto;
import com.example.park_api.resources.dto.mapper.ClienteMapper;
import com.example.park_api.resources.dto.mapper.PageableMapper;
import com.example.park_api.resources.exception.ErrorMessage;
import com.example.park_api.services.ClienteService;
import com.example.park_api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clientes", description = "Contém todas as opereções relativas ao recurso de um cliente")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
public class ClienteResource {

    private final ClienteService clienteService;
    private final UserService usuarioService;

    @Operation(summary = "Criar um novo cliente",
            description = "Recurso para criar um novo cliente vinculado a um usuário cadastrado. " +
                    "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente CPF já possui cadastro no sistema",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") //apenas clientes podem fazer cadastro de cliente
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody @Valid ClienteCreateDTO dto, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Cliente cliente = ClienteMapper.toCliente(dto);
        //recupera informações de user a partir do contexto do Spring Security
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);
        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }

   @Operation(
           summary = "Localizar um cliente",
           description = "Recurso para localizar um cliente pelo ID. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") //apenas o admin tem acesso a esse recurso
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    // Anotação que fornece informações detalhadas sobre a operação da API no Swagger
    @Operation(
            summary = "Recuperar lista de clientes",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(
                            in = QUERY, name = "page",  // Parâmetro para definir a página que será retornada
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),  // Define que o tipo desse parâmetro é inteiro com valor padrão 0
                            description = "Representa a página retornada"  // Descrição do parâmetro
                    ),
                    @Parameter(
                            in = QUERY, name = "size",  // Parâmetro para definir o número de elementos por página
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),  // Define o tamanho padrão de 5 elementos por página
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(
                            in = QUERY, name = "sort", hidden = true,  // Parâmetro para definir a ordenação, mas está oculto na documentação do Swagger
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "nome,asc")),  // Define que a ordenação é feita por 'nome' de forma ascendente
                            description = "Representa a ordenação dos resultados. Aceita múltiplos critérios de ordenação."
                    )
            },
            responses = {  // Definição das possíveis respostas da API
                    @ApiResponse(
                            responseCode = "200", description = "Recurso recuperado com sucesso",  // Retorno de sucesso
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClienteResponseDTO.class))  // O retorno será um objeto ClienteResponseDTO
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",  // Retorno para usuários que não têm a Role 'ADMIN'
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))  // O retorno será um objeto ErrorMessage
                    )
            }
    )

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) //esconde da requisição parametros com hidden true
                                              @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
        Page<ClienteProjection> clientes = clienteService.buscarTodos(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clientes));
    }


   @Operation(summary = "Recuperar dados do cliente autenticado",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClienteResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/details")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDTO> getDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails) {
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }
}
