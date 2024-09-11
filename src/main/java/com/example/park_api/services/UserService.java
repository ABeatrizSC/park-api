package com.example.park_api.services;

import com.example.park_api.entities.User;
import com.example.park_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User salvar(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User buscarPorId(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional
    public User editarSenha(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Password does not match.");
        }
        User user = buscarPorId(id);
        if (!user.getPassword().equals(currentPassword)) {
            throw new RuntimeException("Password does not match.");
        }
        user.setPassword(newPassword);
        return user;
    }

    @Transactional
    public List<User> buscarTodos() {
        return userRepository.findAll();
    }
}
