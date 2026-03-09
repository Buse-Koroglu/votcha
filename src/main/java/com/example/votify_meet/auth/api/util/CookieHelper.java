package com.example.votify_meet.auth.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieHelper {
    @Value("${jwt.refresh-token.expiration-in-seconds}")
    private long refreshTokenExpiration;

    @Value("${app.security.cookie.secure}")
    private boolean isSecure;

    public ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("Lax")
                .maxAge(refreshTokenExpiration)
                .build();
    }

    public ResponseCookie generateLoggedInFlagCookie(boolean isLoggedIn){
        long maxAge = isLoggedIn ? refreshTokenExpiration : 0;
        String value = isLoggedIn ? "true" : "";
        return ResponseCookie.from("logged_in", value)
                .path("/")
                .httpOnly(false)
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken","")
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }
}
