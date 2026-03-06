package com.example.votify_meet.controller;

import com.example.votify_meet.config.JwtService;
import com.example.votify_meet.users.domain.model.Role;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.api.controller.VoteController;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
import com.example.votify_meet.votes.service.VoteService;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
public class VoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private VoteService voteService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String VOTE_ID = "1";
    private final String OPTION_ID = "2";
    private final String USER_ID = "3";
    private VoteResponseDto standardResponse;
    private Users mockUser;

    @BeforeEach
    public void setup() {
        standardResponse = new VoteResponseDto(VOTE_ID,OPTION_ID,null,null);
        mockUser = Users.builder()
                .id(USER_ID)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .role(Role.USER)
                .password("1111").build();

    }


    @Test
    @DisplayName("Create a vote - 302 Created should return.")
    void createVote_returns201_whenValidRequest() throws Exception{
        // Arrange
        VoteRequestDto request = new VoteRequestDto(OPTION_ID);

        // Act
        when(voteService.createVote(request,USER_ID)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(post("/api/votes")
                        .with(csrf())
                        .with(user(mockUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(VOTE_ID))
                .andExpect(jsonPath("$.optionId").value(OPTION_ID));
    }

    @Test
    @DisplayName("Creating vote with invalid request - 400 Bad Request should return")
    void createVote_returns400_whenInvalidRequest() throws Exception{
        // Arrange
        VoteRequestDto request = new VoteRequestDto("");

        // Act & Assert
        mockMvc.perform(post("/api/votes")
                        .with(csrf())
                        .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieve an existing vote - 200 Ok should return")
    void getVote_returns200_whenVoteExist() throws Exception{
        // Arrange
        VoteResponseDto response = new VoteResponseDto(VOTE_ID,OPTION_ID,null,null);

        // Act
        when(voteService.getVote(VOTE_ID)).thenReturn(response);

        // Assert
        mockMvc.perform(get("/api/votes/{id}",VOTE_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VOTE_ID))
                .andExpect(jsonPath("$.optionId").value(OPTION_ID));

    }

    @Test
    @DisplayName("Retrieve non existing vote - 404 Not Found should return")
    void getVote_returns404_whenVoteNotExist() throws Exception{
        // Act
        when(voteService.getVote(VOTE_ID)).thenThrow(new VoteNotFoundException("Vote Not Found"));

        // Assert
        mockMvc.perform(get("/api/votes/{id}",VOTE_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete vote - 200 Ok should return")
    void deleteVote_returns200_whenVoteDeleted() throws  Exception{
        VoteResponseDto response = new VoteResponseDto(VOTE_ID,OPTION_ID,null,null);
        when(voteService.deleteVote(VOTE_ID)).thenReturn(response);
        mockMvc.perform(delete("/api/votes/{id}",VOTE_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VOTE_ID));
    }

    @Test
    @DisplayName("Delete a non-existing Vote - 404 Not Found should return.")
    void deleteVote_returns404_whenVoteNotFound() throws Exception{
        when(voteService.deleteVote(VOTE_ID)).thenThrow(new VoteNotFoundException("Vote Not Found"));
        mockMvc.perform(delete("/api/votes/{id}",VOTE_ID)
                        .with(csrf())
                        .with(user(mockUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Partial vote update - 200 Ok should return.")
    void patchVote_returns200_whenVotePatched() throws Exception{
        // Arrange
        VoteRequestDto request = new VoteRequestDto(VOTE_ID);

        // Act
        when(voteService.updateVote(VOTE_ID,request)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(patch("/api/votes/{id}",VOTE_ID)
                                .with(csrf())
                                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VOTE_ID))
                .andExpect(jsonPath("$.optionId").value(OPTION_ID));
    }

    @Test
    @DisplayName("Partial vote update with invalid data - 400 Bad Request should return.")
    void patchVote_returns400_whenInvalidRequest() throws Exception{
        VoteRequestDto request = new VoteRequestDto("");

        mockMvc.perform(patch("/api/votes/{id}",VOTE_ID)
                        .with(csrf())
                        .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
