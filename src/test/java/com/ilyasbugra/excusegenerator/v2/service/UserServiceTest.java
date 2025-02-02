package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.exception.UsernameAlreadyTakenException;
import com.ilyasbugra.excusegenerator.security.JwtUtil;
import com.ilyasbugra.excusegenerator.v2.dto.UserLoginRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserLoginResponseDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String NEW_USERNAME = "new-user";
    private static final String EXISTING_USERNAME = "existing-user";
    private static final String RAW_PASSWORD = "raw-password";
    private static final String WRONG_PASSWORD = "wrong-password";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String SUCCESSFUL_REGULAR_SIGNUP_MESSAGE = "User successfully signed up with the '" + UserRole.REGULAR + "' role.";
    private static final UserRole REGULAR_ROLE = UserRole.REGULAR;

    private static final String VALID_JWT_TOKEN = "valid-jwt-token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

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

    @Test
    public void testCreateUser_UsernameAlreadyTaken() {
        UserSignUpRequestDTO requestDTO = UserSignUpRequestDTO.builder()
                .username(EXISTING_USERNAME)
                .password(RAW_PASSWORD)
                .build();
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .username(EXISTING_USERNAME)
                .password(ENCODED_PASSWORD)
                .userRole(REGULAR_ROLE)
                .excuses(new ArrayList<>())
                .updatedExcuses(new ArrayList<>())
                .approvedExcuses(new ArrayList<>())
                .build();

        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(existingUser));

        assertThrows(UsernameAlreadyTakenException.class, () -> userService.signUp(requestDTO));

        verify(userRepository).findByUsername(EXISTING_USERNAME);
    }

    @Test
    public void testLoginUser() {
        UserLoginRequestDTO requestDTO = UserLoginRequestDTO.builder()
                .username(EXISTING_USERNAME)
                .password(RAW_PASSWORD)
                .build();
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .username(EXISTING_USERNAME)
                .password(ENCODED_PASSWORD)
                .userRole(REGULAR_ROLE)
                .build();
        UserLoginResponseDTO responseDTO = UserLoginResponseDTO.builder()
                .username(EXISTING_USERNAME)
                .token(VALID_JWT_TOKEN)
                .build();

        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(RAW_PASSWORD, existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(existingUser.getUsername(), existingUser.getUserRole())).thenReturn(VALID_JWT_TOKEN);
        when(userMapper.toUserLoginResponseDTO(existingUser, VALID_JWT_TOKEN)).thenReturn(responseDTO);

        UserLoginResponseDTO userLoginResponseDTO = userService.login(requestDTO);

        assertNotNull(userLoginResponseDTO);
        assertEquals(EXISTING_USERNAME, userLoginResponseDTO.getUsername());
        assertEquals(VALID_JWT_TOKEN, userLoginResponseDTO.getToken());

        verify(userRepository).findByUsername(EXISTING_USERNAME);
        verify(passwordEncoder).matches(RAW_PASSWORD, existingUser.getPassword());
        verify(jwtUtil).generateToken(existingUser.getUsername(), existingUser.getUserRole());
    }

    @Test
    public void testLoginUser_WrongPassword() {
        UserLoginRequestDTO requestDTO = UserLoginRequestDTO.builder()
                .username(EXISTING_USERNAME)
                .password(WRONG_PASSWORD)
                .build();
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .username(EXISTING_USERNAME)
                .password(ENCODED_PASSWORD)
                .userRole(REGULAR_ROLE)
                .build();

        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(WRONG_PASSWORD, existingUser.getPassword())).thenReturn(false);

        // this test probably fail after refactoring...
        // while I was writing the code I didn't realized that the InvalidInputException was coming from excuse exceptions
        // (a bad naming for InvalidInputException) (a lazy work for credential mismatch)
        // that's the reason we are going to do the refactoring!
        // if you wonder why this test fails, this is why! change the InvalidInputException to what I deem worthy while refactoring
        // I'm not going to forget to read this comment block, right? right?
        assertThrows(InvalidInputException.class, () -> userService.login(requestDTO));

        verify(userRepository).findByUsername(EXISTING_USERNAME);
        verify(passwordEncoder).matches(WRONG_PASSWORD, existingUser.getPassword());
    }
}
