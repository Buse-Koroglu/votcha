package com.example.votify_meet.auth.api.controller;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.auth.api.dto.RegisterResponseDto;
import com.example.votify_meet.auth.api.dto.AuthResponseDto;
import com.example.votify_meet.auth.api.util.CookieHelper;
import com.example.votify_meet.auth.service.AuthService;
import com.example.votify_meet.common.logging.BusinessAction;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi{
    private final AuthService authService;
    private final CookieHelper cookieHelper;

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
        ResponseCookie loggedInFlag = cookieHelper.generateLoggedInFlagCookie(true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, loggedInFlag.toString())
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
}
