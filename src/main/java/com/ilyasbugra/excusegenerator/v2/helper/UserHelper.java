package com.ilyasbugra.excusegenerator.v2.helper;

import com.ilyasbugra.excusegenerator.exception.UserNotFoundException;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    private final UserRepository userRepository;

    public UserHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        String username = AuthHelper.getAuthenticatedUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
