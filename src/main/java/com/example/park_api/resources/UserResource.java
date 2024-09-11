package com.example.park_api.resources;

import com.example.park_api.entities.User;
import com.example.park_api.resources.dto.UserCreateDTO;
import com.example.park_api.resources.dto.UserPasswordDTO;
import com.example.park_api.resources.dto.UserResponseDTO;
import com.example.park_api.resources.dto.mapper.UserMapper;
import com.example.park_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserResource {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User newUser = userService.salvar(UserMapper.toUser(userCreateDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        User user = userService.buscarPorId(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO userPasswordDTO) {
        User userUpdated = userService.editarSenha(id,userPasswordDTO.getCurrentPassword(), userPasswordDTO.getNewPassword(), userPasswordDTO.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<User> users = userService.buscarTodos();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}
