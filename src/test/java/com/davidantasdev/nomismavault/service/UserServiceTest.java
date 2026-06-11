package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.mapper.UserMapper;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserNormalizesEmailAndStoresEncodedPassword() {
        UserRequest request = new UserRequest(" Davi ", "DAVI@EXAMPLE.COM ", "plain-password");
        User mappedUser = new User();
        mappedUser.setName(" Davi ");
        mappedUser.setEmail("DAVI@EXAMPLE.COM ");
        mappedUser.setPassword("plain-password");

        when(userRepository.existsByEmail("davi@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class)))
                .thenReturn(new UserResponse(1L, "Davi", "davi@example.com", null, null));

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Davi", savedUser.getName());
        assertEquals("davi@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertNotEquals("plain-password", savedUser.getPassword());
    }
}
