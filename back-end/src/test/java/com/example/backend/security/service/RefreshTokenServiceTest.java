package com.example.backend.security.service;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.security.data.entity.RefreshToken;
import com.example.backend.security.data.repository.RefreshTokenRepository;
import com.example.backend.security.exception.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RefreshTokenServiceTest {

    private RefreshTokenRepository refreshTokenRepository;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", 60000L);
    }

    @Test
    void createRefreshToken_shouldSaveAndReturnToken() {
        UserInfo user = new UserInfo();
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(user);

        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        assertEquals(user, token.getUser());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void findByToken_shouldReturnOptionalToken() {
        String tokenStr = UUID.randomUUID().toString();
        RefreshToken expectedToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(expectedToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenStr);

        assertTrue(result.isPresent());
        assertEquals(expectedToken, result.get());
    }

    @Test
    void verifyExpiration_shouldReturnToken_ifNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));
        RefreshToken result = refreshTokenService.verifyExpiration(token);
        assertEquals(token, result);
    }

    @Test
    void verifyExpiration_shouldThrowAndDelete_ifExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60));
        assertThrows(TokenExpiredException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void deleteByUser_shouldDeleteCorrectly() {
        UserInfo user = new UserInfo();
        refreshTokenService.deleteByUser(user);
        verify(refreshTokenRepository).deleteByUser(user);
    }

}
