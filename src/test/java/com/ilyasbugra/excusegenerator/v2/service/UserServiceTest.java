package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.v2.mapper.UserMapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        UserSignUpRequestDTO requestDTO = UserSignUpRequestDTO.builder()
                .username("new-user")
                .password("raw-password")
                .build();
        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .username("new-user")
                .password("encoded-password")
                .userRole(UserRole.REGULAR)
                .excuses(new ArrayList<>())
                .updatedExcuses(new ArrayList<>())
                .approvedExcuses(new ArrayList<>())
                .build();
        UserSignUpResponseDTO responseDTO = UserSignUpResponseDTO.builder()
                .username("new-user")
                .message("User successfully signed up with the '" + UserRole.REGULAR + "' role.")
                .build();

        when(userRepository.findByUsername("new-user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
        when(userMapper.toUser(requestDTO, "encoded-password", UserRole.REGULAR)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserSignUpResponseDTO(any(User.class), anyString())).thenReturn(responseDTO);

        UserSignUpResponseDTO userSignUpResponseDTO = userService.signUp(requestDTO);

        assertNotNull(userSignUpResponseDTO);
        assertEquals("new-user", userSignUpResponseDTO.getUsername());
        assertEquals("User successfully signed up with the '" + UserRole.REGULAR + "' role.", userSignUpResponseDTO.getMessage());

        verify(userRepository).save(any(User.class));
    }
}
