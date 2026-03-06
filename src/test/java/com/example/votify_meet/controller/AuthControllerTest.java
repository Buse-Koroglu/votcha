package com.example.votify_meet.controller;

import com.example.votify_meet.auth.AuthRequestDto;
import com.example.votify_meet.auth.AuthResponseDto;
import com.example.votify_meet.auth.AuthenticationController;
import com.example.votify_meet.auth.AuthenticationService;
import com.example.votify_meet.config.JwtService;
import com.example.votify_meet.config.SecurityConfig;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false) // prevents 401 Unauthorized Error (/api/auth/**).permitAll()
public class AuthControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthenticationService  authenticationService;
    @MockitoBean private JwtService  jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

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
        AuthResponseDto expectedResponse = new AuthResponseDto("jwt-token", "User successfully login");

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

}
