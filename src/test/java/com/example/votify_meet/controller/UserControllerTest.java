package com.example.votify_meet.controller;

import com.example.votify_meet.config.JwtService;
import com.example.votify_meet.users.api.controller.UsersController;
import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Role;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private UsersService usersService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String USER_ID = "1";
    private final String FIRST_NAME = "Jack";
    private final String LAST_NAME = "London";
    private final String EMAIL = "jack@gmail.com";

    private Users mockUser;
    private UsersResponseDto standardResponse;

    @BeforeEach
    void setUp() {
        standardResponse = new UsersResponseDto(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, null, null);
        mockUser = Users.builder()
                .id(USER_ID)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .role(Role.USER)
                .password("1111").build();

    }

    @Test
    @DisplayName("Successful User Creation - 201 Created should returned")
    void createUser_returns201_whenValidRequest() throws Exception {
        // Arrange
        UsersRequestDto request = new UsersRequestDto(FIRST_NAME, LAST_NAME, EMAIL, "145*9o");

        // Act
        when(usersService.createUser(any(UsersRequestDto.class))).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @DisplayName("Creating a user with missing data -400 Bad Request should return.")
    void createUser_returns400_whenInvalidRequest() throws Exception {
        // Arrange
        UsersRequestDto request = new UsersRequestDto(FIRST_NAME, "", EMAIL, ""); // Invalid request

        // Assert
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieve an existing user - 200 OK should return.")
    void getUser_returns200_whenUserExist() throws Exception {
        // Act
        when(usersService.getUser(USER_ID)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(get("/api/users/{id}", USER_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME));
    }

    @Test
    @DisplayName("Retrieving Non-Existing user - 404 Not Found should return.")
    void getUser_returns404_whenUserNotExist() throws Exception {
        // Act
        when(usersService.getUser("2")).thenThrow(new UserNotFoundException("User Not Found"));

        // Assert
        mockMvc.perform(get("/api/users/{id}", "2")
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("User Deletion - 200 OK should return.")
    void deleteUser_returns200_whenUserDeleted() throws Exception {
        // Act
        when(usersService.deleteUser(USER_ID)).thenReturn(standardResponse);

        // Arrange
        mockMvc.perform(delete("/api/users/{id}", USER_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID));
    }

    @Test
    @DisplayName("Delete Non-Existing User - 404 Not Found should return.")
    void deleteUser_returns404_whenUserNotFound() throws Exception {
        // Act
        when(usersService.deleteUser(USER_ID)).thenThrow(new UserNotFoundException("User Not Found"));

        // Assert
        mockMvc.perform(delete("/api/users/{id}", USER_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Partial User Update- 200 OK should return")
    void patchUser_returns200_whenUserPatched() throws Exception {
        // Arrange
        UpdateUsersRequestDto request = new UpdateUsersRequestDto(FIRST_NAME, LAST_NAME, EMAIL, "145*9o");

        // Act
        when(usersService.patchUser(eq(USER_ID), any(UpdateUsersRequestDto.class))).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(patch("/api/users/{id}", USER_ID)
                        .with(csrf())
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @DisplayName("Partial User Update with Invalid Data - 400 Bad Request should return")
    void patchUser_returns400_whenInvalidRequest() throws Exception {
        UpdateUsersRequestDto request = new UpdateUsersRequestDto("", "", "", "");

        mockMvc.perform(patch("/api/users/{id}", USER_ID)
                        .with(csrf())
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
