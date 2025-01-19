package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.UsernameAlreadyTakenException;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.v2.mapper.UserMapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserSignUpResponseDTO signUp(UserSignUpRequestDTO requestDTO) {
        logger.debug("Checking if username '{}' is already taken", requestDTO.getUsername());

        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            logger.warn("Username '{}' is already taken", requestDTO.getUsername());
            throw new UsernameAlreadyTakenException(requestDTO.getUsername());
        }

        logger.info("Hashing password for username '{}'", requestDTO.getUsername());
        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());

        // TODO: I need to work on the mapper to fix this part.. but for now I just want to start working on login 😎
        User user = User.builder()
                .username(requestDTO.getUsername())
                .password(hashedPassword)
                .userRole(UserRole.REGULAR)
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User '{}' saved with '{}' role.", savedUser.getUsername(), savedUser.getUserRole());

        UserSignUpResponseDTO signUpResponseDTO = userMapper.toUserSignUpResponseDTO(savedUser);
        signUpResponseDTO.setMessage("User successfully signed up with the '" + savedUser.getUserRole() + "' role.");

        return signUpResponseDTO;
    }
}
