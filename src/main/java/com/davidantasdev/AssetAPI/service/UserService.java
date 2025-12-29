package com.davidantasdev.AssetAPI.service;

import com.davidantasdev.AssetAPI.dto.request.UserRequest;
import com.davidantasdev.AssetAPI.dto.response.UserResponse;
import com.davidantasdev.AssetAPI.entity.User;
import com.davidantasdev.AssetAPI.mapper.UserMapper;
import com.davidantasdev.AssetAPI.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com email: " + email));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    public UserResponse createUser(UserRequest userRequest) {
        String email = userRequest.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado: " + email);
        }

        User user = new User();
        user.setName(userRequest.name().trim());
        user.setEmail(email);
        user.setPassword(userRequest.password());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com ID: " + id));
        if (userRequest.name() != null && !userRequest.name().isEmpty()) {
            user.setName(userRequest.name().trim());
        }
        if (userRequest.email() != null && !userRequest.email().isEmpty()) {
            String newEmail = userRequest.email().trim().toLowerCase();
            if (!user.getEmail().equalsIgnoreCase(newEmail) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email já está em uso");
            }
            user.setEmail(newEmail);
        }
        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            user.setPassword(userRequest.password());  // TODO: Adicionar hash de senha em produção
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com ID: " + id));
        userRepository.delete(user);
    }
}
