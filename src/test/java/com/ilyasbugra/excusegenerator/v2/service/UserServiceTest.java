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

    private static final String NEW_USERNAME = "new-user";
    private static final String RAW_PASSWORD = "raw-password";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String SUCCESSFUL_REGULAR_SIGNUP_MESSAGE = "User successfully signed up with the '" + UserRole.REGULAR + "' role.";
    private static final UserRole REGULAR_ROLE = UserRole.REGULAR;

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
                .username(NEW_USERNAME)
                .password(RAW_PASSWORD)
                .build();
        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .username(NEW_USERNAME)
                .password(ENCODED_PASSWORD)
                .userRole(REGULAR_ROLE)
                .excuses(new ArrayList<>())
                .updatedExcuses(new ArrayList<>())
                .approvedExcuses(new ArrayList<>())
                .build();
        UserSignUpResponseDTO responseDTO = UserSignUpResponseDTO.builder()
                .username(NEW_USERNAME)
                .message(SUCCESSFUL_REGULAR_SIGNUP_MESSAGE)
                .build();

        when(userRepository.findByUsername(NEW_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userMapper.toUser(requestDTO, ENCODED_PASSWORD, REGULAR_ROLE)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserSignUpResponseDTO(any(User.class), anyString())).thenReturn(responseDTO);

        UserSignUpResponseDTO userSignUpResponseDTO = userService.signUp(requestDTO);

        assertNotNull(userSignUpResponseDTO);
        assertEquals(NEW_USERNAME, userSignUpResponseDTO.getUsername());
        assertEquals(SUCCESSFUL_REGULAR_SIGNUP_MESSAGE, userSignUpResponseDTO.getMessage());

        verify(userRepository).save(any(User.class));
    }
}
