package com.example.votify_meet;

import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.options.api.controller.OptionController;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OptionController.class)
public class OptionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private OptionService optionService;
    @MockitoBean
    private OptionMapper optionMapper;

    @Test
    void createOption_returns201_whenValidRequest() throws Exception{
        OptionRequestDto request = new OptionRequestDto("A");
        OptionResponseDto response = new OptionResponseDto("1","A",null,null);
        when(optionService.createOption(request,"1","2")).thenReturn(response);
        mockMvc.perform(post("/api/events/{id}/options","1")
                        .header("X-User-Id","2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));
    }

    @Test
    void createOption_returns400_whenInValidRequest() throws Exception{
        OptionRequestDto request = new OptionRequestDto("");
        mockMvc.perform(post("/api/events/{id}/options","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOption_returns200_whenOptionExist() throws Exception{
        OptionResponseDto response = new OptionResponseDto("1","A",null,null);
        when(optionService.getOption("1")).thenReturn(response);
        mockMvc.perform(get("/api/options/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

    }

    @Test
    void getOptions_returns404_whenOptionNotExist() throws Exception{
        when(optionService.getOption("1")).thenThrow(new OptionNotFoundException("Option Not Found"));
        mockMvc.perform(get("/api/options/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOption_returns200_whenOptionDeleted() throws  Exception{
        OptionResponseDto response = new OptionResponseDto("1","A",null,null);
        when(optionService.deleteOption("1")).thenReturn(response);
        mockMvc.perform(delete("/api/options/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"));
    }

    @Test
    void deleteOption_returns404_whenOptionNotFound() throws Exception{
        when(optionService.deleteOption("1")).thenThrow(new OptionNotFoundException("Option Not Found"));
        mockMvc.perform(delete("/api/options/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchOption_returns200_whenOptionPatched() throws Exception{
        UpdateOptionRequestDto request = new UpdateOptionRequestDto("A");
        OptionResponseDto response = new OptionResponseDto("1","A",null,null);

        when(optionService.patchOption("1",request)).thenReturn(response);
        mockMvc.perform(patch("/api/options/{id}","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"));
    }
}
