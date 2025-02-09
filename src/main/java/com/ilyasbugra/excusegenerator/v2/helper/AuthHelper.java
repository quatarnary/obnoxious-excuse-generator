package com.ilyasbugra.excusegenerator.v2.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

    private static final Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    public static String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            logger.error("Authentication is null or empty: {}", authentication != null ? authentication.getName() : "auth is null");
            throw new IllegalStateException("Authentication is null or empty");
        }
        return authentication.getName();
    }
}
