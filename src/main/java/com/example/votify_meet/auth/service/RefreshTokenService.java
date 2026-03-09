package com.example.votify_meet.auth.service;

import com.example.votify_meet.auth.domain.exception.TokenExpiredException;
import com.example.votify_meet.auth.domain.exception.TokenNotFoundException;
import com.example.votify_meet.auth.domain.exception.TokenRevokedException;
import com.example.votify_meet.auth.domain.model.RefreshToken;
import com.example.votify_meet.auth.domain.repository.RefreshTokenRepository;
import com.example.votify_meet.users.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(Users user){
            String token = generateRefreshToken();
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(Instant.now().plusSeconds(60*60*24*7))
                    .isRevoked(false)
                    .build();
            return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if(refreshToken.isRevoked()){
            throw new TokenRevokedException("Token revoked");
        }
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            throw new TokenExpiredException("Token expired");
        }
        return refreshToken;
    }

    private String generateRefreshToken(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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
