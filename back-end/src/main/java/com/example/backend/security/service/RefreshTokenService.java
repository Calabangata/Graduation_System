package com.example.backend.security.service;

import com.example.backend.data.entity.UserInfo;
import com.example.backend.security.data.entity.RefreshToken;
import com.example.backend.security.data.repository.RefreshTokenRepository;
import com.example.backend.security.exception.TokenExpiredException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${security.jwt.refresh-expiration-time}")
    private Long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(UserInfo user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public void deleteByUser(UserInfo user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
