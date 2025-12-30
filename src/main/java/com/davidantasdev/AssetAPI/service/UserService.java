package com.davidantasdev.AssetAPI.service;

import com.davidantasdev.AssetAPI.dto.request.UserRequest;
import com.davidantasdev.AssetAPI.dto.response.UserResponse;
import com.davidantasdev.AssetAPI.entity.User;
import com.davidantasdev.AssetAPI.exception.BusinessException;
import com.davidantasdev.AssetAPI.exception.ResourceNotFoundException;
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
