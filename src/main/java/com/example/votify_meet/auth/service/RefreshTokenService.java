package com.example.votify_meet.auth.service;

import com.example.votify_meet.auth.domain.exception.TokenExpiredException;
import com.example.votify_meet.auth.domain.exception.TokenNotFoundException;
import com.example.votify_meet.auth.domain.exception.TokenRevokedException;
import com.example.votify_meet.auth.domain.model.RefreshToken;
import com.example.votify_meet.auth.domain.repository.RefreshTokenRepository;
import com.example.votify_meet.users.domain.model.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration-in-seconds}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(Users user){
        String token = generateRefreshToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpiration))
                .isRevoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validateAndRotate(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if(refreshToken.isRevoked()){
            throw new TokenRevokedException("Token revoked");
        }
        // delete from db if token is expired
        if(isTokenExpired(refreshToken)){
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Token expired");
        }
        // Rotation
        refreshToken.setRevoked(true);
        return refreshTokenRepository.save(refreshToken);
    }

    private String generateRefreshToken(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


    public boolean isTokenExpired(RefreshToken token){
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void revokeRefreshToken(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
    public void revokeByToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token Not Found"));
        revokeRefreshToken(refreshToken);
    }
}
