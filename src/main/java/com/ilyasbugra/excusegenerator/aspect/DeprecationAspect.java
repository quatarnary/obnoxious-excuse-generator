package com.ilyasbugra.excusegenerator.aspect;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DeprecationAspect {

    private final HttpServletResponse response;

    public DeprecationAspect(HttpServletResponse response) {
        this.response = response;
    }

    @Before("execution(* com.ilyasbugra.excusegenerator.v1.controller..*(..))")
    public void addDeprecationHeader() {
        response.addHeader(HttpHeaders.WARNING, "299 - This endpoint is deprecated. Please migrate to /api/v2.");
    }
}
