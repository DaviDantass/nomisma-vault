package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.UserMapper;
import com.davidantasdev.nomismavault.repository.UserRepository;
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

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com email: " + email));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        if (userRequest == null) {
            throw new IllegalArgumentException("UserRequest não pode ser null");
        }
        String email = userRequest.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado: " + email);
        }
        User user = userMapper.toEntity(userRequest);
        user.setEmail(email);
        user.setName(user.getName().trim());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }


    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {

        if (userRequest == null) {
            throw new IllegalArgumentException("UserRequest não pode ser null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com ID: " + id));
        
        String newEmail = null;
        if (userRequest.email() != null && !userRequest.email().isBlank()) {
            newEmail = userRequest.email().trim().toLowerCase();

            if (!user.getEmail().equalsIgnoreCase(newEmail)
                    && userRepository.existsByEmail(newEmail)) {
                throw new BusinessException("Email já está em uso");
            }
        }

        userMapper.updateEntityFromRequest(userRequest, user);

        if (newEmail != null) {
            user.setEmail(newEmail);
        }
        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }

        return userMapper.toResponse(user);
    }


    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com ID: " + id));
        userRepository.delete(user);
    }
}
