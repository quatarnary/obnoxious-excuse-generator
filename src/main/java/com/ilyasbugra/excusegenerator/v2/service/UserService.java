package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.exception.UserNotFoundException;
import com.ilyasbugra.excusegenerator.exception.UsernameAlreadyTakenException;
import com.ilyasbugra.excusegenerator.infrastructure.security.JwtUtil;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.model.UserRole;
import com.ilyasbugra.excusegenerator.util.UserErrorMessages;
import com.ilyasbugra.excusegenerator.v2.dto.UserLoginRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserLoginResponseDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.v2.mapper.UserMapper;
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
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public UserSignUpResponseDTO signUp(UserSignUpRequestDTO requestDTO) {
        logger.debug("Checking if username '{}' is already taken", requestDTO.getUsername());

        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            logger.warn("Username '{}' is already taken", requestDTO.getUsername());
            throw new UsernameAlreadyTakenException(requestDTO.getUsername());
        }

        logger.info("Hashing password for username '{}'", requestDTO.getUsername());
        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());

        User user = userMapper.toUser(requestDTO, hashedPassword, UserRole.REGULAR);

        logger.debug("Saving user:.. '{}'", requestDTO.getUsername());
        User savedUser = userRepository.save(user);
        logger.info("User '{}' saved with '{}' role.", savedUser.getUsername(), savedUser.getUserRole());

        // the message may and will probably change with token, but for now just as a skelly we have message..
        return userMapper.toUserSignUpResponseDTO(
                savedUser,
                "User successfully signed up with the '" + savedUser.getUserRole() + "' role."
        );
    }

    public UserLoginResponseDTO login(UserLoginRequestDTO requestDTO) {
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException(requestDTO.getUsername()));
        logger.info("User '{}' found. Hashing magic to validate password...", user.getUsername());

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            logger.warn("User '{}' provided the invalid 'password' spell...", user.getUsername());
            // TODO: maybe not so much of a todo but idea.. create InvalidCredentialException
            throw new InvalidInputException(UserErrorMessages.INVALID_CREDENTIALS);
        }

        logger.info("Generating JWT spell for user: '{}'", user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserRole());

        return userMapper.toUserLoginResponseDTO(user, token);
    }
}
