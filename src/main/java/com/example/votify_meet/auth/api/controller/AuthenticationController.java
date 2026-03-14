package com.example.votify_meet.auth.api.controller;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.auth.api.dto.RegisterResponseDto;
import com.example.votify_meet.auth.api.dto.TokenResponseDto;
import com.example.votify_meet.auth.api.dto.AuthResponseDto;
import com.example.votify_meet.auth.api.util.CookieHelper;
import com.example.votify_meet.auth.service.AuthenticationService;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication Management APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final CookieHelper cookieHelper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDto register(
            @Valid @RequestBody UsersRequestDto request){
        return authenticationService.register(request);

    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenResponseDto> authenticate(
            @RequestBody AuthRequestDto request,
            HttpServletResponse response
    ){
          AuthResponseDto loginResponse = authenticationService.login(request);
          ResponseCookie refreshCookie = cookieHelper.generateRefreshTokenCookie(loginResponse.refreshToken());
          ResponseCookie loggedInFlag = cookieHelper.generateLoggedInFlagCookie(true);

          return ResponseEntity.ok()
                  .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                  .header(HttpHeaders.SET_COOKIE, loggedInFlag.toString())
                  .body(new TokenResponseDto(loginResponse.accessToken(), loginResponse.message()));
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto refresh(@CookieValue("refreshToken") String refreshToken){
       return authenticationService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        authenticationService.logout(refreshToken);
        ResponseCookie refreshCookie = cookieHelper.getCleanRefreshTokenCookie();
        ResponseCookie loggedInFlag = cookieHelper.generateLoggedInFlagCookie(false);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, loggedInFlag.toString())
                .build();
    }
}
