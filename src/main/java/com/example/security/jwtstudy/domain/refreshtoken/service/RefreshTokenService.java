package com.example.security.jwtstudy.domain.refreshtoken.service;

import com.example.security.jwtstudy.domain.refreshtoken.entity.RefreshToken;
import com.example.security.jwtstudy.domain.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByTokenFetch(String token) {
        return refreshTokenRepository.findByTokenFetch(token);
    }

    @Transactional
    public void update(RefreshToken refreshToken, String token, Instant expiryDate) {
        refreshToken.updateToken(token, expiryDate);
    }
}
