package com.example.park_api.services;

import com.example.park_api.entities.User;
import com.example.park_api.exception.EntityNotFoundException;
import com.example.park_api.exception.PasswordInvalidException;
import com.example.park_api.repositories.UserRepository;
import com.example.park_api.exception.UsernameUniqueViolationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User salvar(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", user.getUsername()));
        }
    }

    @Transactional
    public User buscarPorId(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado.", id))
        );
    }

    @Transactional
    public User editarSenha(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Sua senha não confere.");
        }
        User user = buscarPorId(id);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("Sua senha não confere.");
        }
        user.setPassword(newPassword);
        return user;
    }

    @Transactional
    public List<User> buscarTodos() {
        return userRepository.findAll();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public User buscarPorUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User com '%s' não encontrado", username))
        );
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public User.Role buscarRolePorUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
