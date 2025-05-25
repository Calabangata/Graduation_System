package com.example.backend.security.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;

    private final String SECRET_KEY = "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXl4eHl4eHl4eHg="; // base256 of "fakesecretkeyfakesecretkey"
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_TIME);

    }

    @Test
    void generateToken_and_validateToken_success() {

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void tokenShouldBeInvalid_whenUserMismatch() {

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("wrong@example.com");

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    void getExpirationTime_shouldReturnConfiguredValue() {
        assertEquals(EXPIRATION_TIME, jwtService.getExpirationTime());
    }

    @Test
    void extractClaim_shouldExtractExpiration() {

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtService.generateToken(userDetails);
        var exp = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(exp);
    }

    @Test
    void generateToken_withExtraClaims_shouldIncludeClaims() {

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        Map<String, Object> claims = Map.of("role", "STUDENT");
        String token = jwtService.generateToken(claims, userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

}
