package com.ilyasbugra.excusegenerator.security;

import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private static final String TEST_SECRET = "this-is-a-very-secret-key-and-it-is-32-char";
    private static final String TEST_USERNAME = "test-user";
    private static final UserRole TEST_ROLE = UserRole.REGULAR;

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET);
    }

    @Test
    public void testGenerateToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_ROLE);

        assertNotNull(token);
    }

    @Test
    public void testValidateToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_ROLE);

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    public void testExtractUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_ROLE);
        String extractedUsername = jwtUtil.extractClaim(token, Claims::getSubject);

        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    public void testExpiredToken() {
        String expiredToken = generateExpiredToken();

        assertTrue(jwtUtil.isTokenExpired(expiredToken));
    }

    // Helpers
    private String generateExpiredToken() {
        return Jwts.builder()
                .subject(TEST_USERNAME)
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .compact();
    }
}
