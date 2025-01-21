package com.ilyasbugra.excusegenerator.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // csrf: Cross-Site Request Forgery -> they say it is not needed because of the JWT!
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v2/users/login").permitAll()
                        .requestMatchers("/api/v2/users/sign-up").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/excuses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/excuses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/excuses").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/excuses/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/excuses/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
