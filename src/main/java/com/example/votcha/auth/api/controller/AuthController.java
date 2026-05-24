package com.example.votcha.auth.api.controller;

import com.example.votcha.auth.api.dto.AuthRequestDto;
import com.example.votcha.auth.api.dto.RegisterResponseDto;
import com.example.votcha.auth.api.dto.AuthResponseDto;
import com.example.votcha.auth.api.util.CookieHelper;
import com.example.votcha.auth.service.AuthService;
import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.users.api.dto.UsersRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi{
    private final AuthService authService;
    private final CookieHelper cookieHelper;

    @Value("${spring.mail.frontend.url}")private String frontendUrl;

    @BusinessAction(action = "USER_REGISTERED", domain = "AUTH")
    @Override
    public RegisterResponseDto register(UsersRequestDto request){
        return authService.register(request);

    }

    @BusinessAction(action = "USER_LOGIN", domain = "AUTH")
    @Override
    public ResponseEntity<AuthResponseDto> authenticate( AuthRequestDto request) {
        AuthResponseDto loginResponse = authService.login(request);
        ResponseCookie refreshCookie = cookieHelper.generateRefreshTokenCookie(loginResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDto(loginResponse.accessToken(), loginResponse.refreshToken(), loginResponse.message()));

    }


    @Override
    public ResponseEntity<AuthResponseDto> refresh(String refreshToken){
        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthResponseDto responseDto = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = cookieHelper.generateRefreshTokenCookie(responseDto.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseDto
                );
    }


    @BusinessAction(action = "USER_LOGOUT", domain = "AUTH")
    @Override
    public ResponseEntity<Void> logout(String refreshToken) {
        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .headers(cookieHelper.getLogoutHeaders())
                .build();
    }

    @Override
    public ResponseEntity<String> verifyAccount(String token, HttpServletResponse response) throws IOException {
        authService.verifyUser(token);
        response.sendRedirect(frontendUrl+"/login?verified=true");
        return ResponseEntity.ok("Verification link sent");
    }
}
