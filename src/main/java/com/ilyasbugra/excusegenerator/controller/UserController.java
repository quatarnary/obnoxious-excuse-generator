package com.ilyasbugra.excusegenerator.controller;


import com.ilyasbugra.excusegenerator.dto.UserLoginRequestDTO;
import com.ilyasbugra.excusegenerator.dto.UserLoginResponseDTO;
import com.ilyasbugra.excusegenerator.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserSignUpResponseDTO> signUp(@Valid @RequestBody UserSignUpRequestDTO request) {
        logger.info("Sign up request: {}", request.getUsername());

        UserSignUpResponseDTO responseDTO = userService.signUp(request);

        logger.info("User '{}' successfully signed up", responseDTO.getUsername());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO request) {
        logger.info("Login request: {}", request.getUsername());

        UserLoginResponseDTO responseDTO = userService.login(request);

        logger.info("User '{}' successfully logged in", responseDTO.getUsername());
        return ResponseEntity.ok(responseDTO);
    }
}
