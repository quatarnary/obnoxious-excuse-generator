package com.ilyasbugra.excusegenerator.config;

import com.ilyasbugra.excusegenerator.security.JwtAuthenticationFilter;
import com.ilyasbugra.excusegenerator.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public Random random() {
        return new Random();
    }
}
