package com.example.votcha.auth.api.controller;

import com.example.votcha.auth.api.dto.AuthRequestDto;
import com.example.votcha.auth.api.dto.AuthResponseDto;
import com.example.votcha.auth.api.dto.RegisterResponseDto;
import com.example.votcha.users.api.dto.UsersRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth", description = "Authentication Management APIs")
@RequestMapping("/api/auth")
public interface AuthApi {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponseDto register(@Valid @RequestBody UsersRequestDto request);

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request);

    @PostMapping("/refresh")
    ResponseEntity<AuthResponseDto> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken);

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken);

    @GetMapping("/verify")
    ResponseEntity<String> verifyAccount(@RequestParam(name = "token") String token, HttpServletResponse response) throws IOException;
}

