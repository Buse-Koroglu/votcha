package com.example.votify_meet.auth.api.controller;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.auth.api.dto.AuthResponseDto;
import com.example.votify_meet.auth.api.dto.LoginResponseDto;
import com.example.votify_meet.auth.service.AuthenticationService;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication Management APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(
            @Valid @RequestBody UsersRequestDto request){

        // todo - XSS protection
        return authenticationService.register(request);

    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDto authenticate(
            @RequestBody AuthRequestDto request,
            HttpServletResponse response
    ){
          LoginResponseDto loginResponse = authenticationService.login(request);
          ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.refreshToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(60 * 60 * 24 * 7) // 7 days for refresh token
                .build();
        ResponseCookie loggedInFlag = ResponseCookie.from("logged_in", "true")
                .path("/")
                .httpOnly(false)
                .maxAge(60 * 60 * 24 * 7)
                .build();
          response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
          response.addHeader(HttpHeaders.SET_COOKIE, loggedInFlag.toString());
          return new AuthResponseDto(loginResponse.accessToken(), loginResponse.message());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDto refresh(@CookieValue("refreshToken") String refreshToken){
       return authenticationService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletResponse response, @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        authenticationService.logout(refreshToken);
        // logout then clear cookies
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        ResponseCookie flag = ResponseCookie.from("logged_in", "")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, flag.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
