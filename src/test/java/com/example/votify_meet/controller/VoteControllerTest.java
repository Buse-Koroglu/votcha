package com.example.votify_meet;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.votes.api.controller.VoteController;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.api.mapper.VoteMapper;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
import com.example.votify_meet.votes.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VoteController.class)
public class VoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private VoteService voteService;
    @MockitoBean
    private VoteMapper voteMapper;

    @Test
    void createVote_returns201_whenValidRequest() throws Exception{
        VoteRequestDto request = new VoteRequestDto("2");
        VoteResponseDto response = new VoteResponseDto("1","2",null,null);
        when(voteService.createVote(request,"3")).thenReturn(response);

        mockMvc.perform(post("/api/votes")
                    .header("X-User-Id","3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.optionId").value("2"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));




    }

    @Test
    void createVote_returns400_whenInvalidRequest() throws Exception{
        VoteRequestDto request = new VoteRequestDto("");
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVote_returns200_whenVoteExist() throws Exception{
        VoteResponseDto response = new VoteResponseDto("1","2",null,null);
        when(voteService.getVote("1")).thenReturn(response);
        mockMvc.perform(get("/api/votes/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.optionId").value("2"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

    }

    @Test
    void getVote_returns404_whenVoteNotExist() throws Exception{
        when(voteService.getVote("1")).thenThrow(new VoteNotFoundException("Vote Not Found"));
        mockMvc.perform(get("/api/votes/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVote_returns200_whenVoteDeleted() throws  Exception{
        VoteResponseDto response = new VoteResponseDto("1","2",null,null);
        when(voteService.deleteVote("1")).thenReturn(response);
        mockMvc.perform(delete("/api/votes/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.optionId").value("2"));
    }

    @Test
    void deleteVote_returns404_whenVoteNotFound() throws Exception{
        when(voteService.deleteVote("1")).thenThrow(new VoteNotFoundException("Vote Not Found"));
        mockMvc.perform(delete("/api/votes/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchVote_returns200_whenVotePatched() throws Exception{
        VoteRequestDto request = new VoteRequestDto("1");
        VoteResponseDto response = new VoteResponseDto("1","2",null,null);

        when(voteService.updateVote("1",request)).thenReturn(response);
        mockMvc.perform(patch("/api/votes/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.optionId").value("2"));
    }


}
