package com.example.votify_meet.controller;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.auth.api.dto.AuthResponseDto;
import com.example.votify_meet.auth.api.controller.AuthenticationController;
import com.example.votify_meet.auth.api.dto.LoginResponseDto;
import com.example.votify_meet.auth.domain.exception.TokenExpiredException;
import com.example.votify_meet.auth.domain.exception.TokenNotFoundException;
import com.example.votify_meet.auth.domain.exception.TokenRevokedException;
import com.example.votify_meet.auth.service.AuthenticationService;
import com.example.votify_meet.auth.service.RefreshTokenService;
import com.example.votify_meet.config.JwtService;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false) // prevents 401 Unauthorized Error (/api/auth/**).permitAll()
public class AuthControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthenticationService  authenticationService;
    @MockitoBean private JwtService  jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private RefreshTokenService refreshTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AuthResponseDto successResponse;

    @BeforeEach
    public void setup() {
        successResponse = new AuthResponseDto("jwt-token", "Operation successful");
    }

    @Test
    @DisplayName("Register - Should return 201 when request is valid")
    void register_returns201_whenValidRequest() throws Exception {
        // Arrange
        UsersRequestDto request = new UsersRequestDto("Enes", "Test", "enes@test.com", "password123");
        when(authenticationService.register(any(UsersRequestDto.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.token").value("jwt-token"))
                        .andExpect(jsonPath("$.message").value("Operation successful"));
    }

    @Test
    @DisplayName("Login - Should return 200 and token when credentials are valid")
    void login_returns200_whenCredentialsAreValid() throws Exception {
        // Arrange
        AuthRequestDto loginRequest = new AuthRequestDto("enes@test.com", "password123");
        LoginResponseDto expectedResponse = new LoginResponseDto("jwt-token", "jwt-refresh","User successfully login");

        // Act
        when(authenticationService.login(any(AuthRequestDto.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.message").value("User successfully login"));

        // Assert
        verify(authenticationService, times(1)).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Refresh - Should return new access token when refresh token is valid")
    void refresh_returnsNewToken_whenCookieIsValid() throws Exception {
        // Arrange
        String validRefreshToken = "refresh-token";
        AuthResponseDto expectedResponse = new AuthResponseDto("access-token", "Access token refreshed");

        when(authenticationService.refresh(validRefreshToken)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", validRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.message").value("Access token refreshed"));
    }

    @Test
    @DisplayName("Logout - Should revoke token and clear cookies")
    void logout_revokeTokenAndClearCookies() throws Exception {
        String existingToken = "token";
        mockMvc.perform(post("/api/auth/logout")
                .cookie(new Cookie("refreshToken",existingToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("refreshToken", ""))
                .andExpect(cookie().maxAge("refreshToken", 0))
                .andExpect(cookie().value("logged_in", ""))
                .andExpect(cookie().maxAge("logged_in", 0));
        verify(authenticationService, times(1)).logout(existingToken);
    }

    @Test
    @DisplayName("Refresh - Should return error when token is not found in db")
    void refresh_returnsError_whenTokenNotFound() throws Exception {
        // Arrange
        when(authenticationService.refresh(anyString()))
                .thenThrow(new TokenNotFoundException("Token Not Found"));
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "not-exist-token")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Token Not Found"));
    }

    @Test
    @DisplayName("Refresh - Should return 401 when token expired")
    void refresh_returns401_whenTokenExpired() throws Exception {
        // Arrange
        when(authenticationService.refresh(anyString()))
                .thenThrow(new TokenExpiredException("Token expired"));
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "expired-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }

    @Test
    @DisplayName("Refresh - Should return 403 when token is revoked")
    void refresh_returns403_whenTokenRevoked() throws Exception {
        // Arrange
        when(authenticationService.refresh(anyString()))
                .thenThrow(new TokenRevokedException("Token revoked"));
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "revoked-token")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Token revoked"));
    }



}
