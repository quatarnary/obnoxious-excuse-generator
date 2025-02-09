package com.ilyasbugra.excusegenerator.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DeprecationInterceptor implements HandlerInterceptor {

    public static final Logger logger = LoggerFactory.getLogger(DeprecationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/api/v1")) {
            logger.warn("\uD83D\uDEA8 Excommunicado Warning: {} is deprecated.", request.getRequestURI());
        }

        return true;
    }
}
