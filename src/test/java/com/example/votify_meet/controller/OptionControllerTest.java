package com.example.votify_meet.controller;

import com.example.votify_meet.options.api.controller.OptionController;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.service.OptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private final String EVENT_ID = "1";
    private final String OPTION_ID = "1";
    private final String USER_ID = "2";
    private OptionResponseDto standardResponse;

    @BeforeEach
    public void setup() {
        standardResponse = new OptionResponseDto("1","A",null,null);
    }

    @Test
    @DisplayName("Successful Option Creation - 201 Created should be returned")
    void createOption_returns201_whenValidRequest() throws Exception{
        // Arrange
        OptionRequestDto request = new OptionRequestDto("A");

        // Act
        when(optionService.createOption(any(OptionRequestDto.class), eq(EVENT_ID), eq(USER_ID))).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(post("/api/events/{id}/options","1")
                        .header("X-User-Id",USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Creating an option with an invalid request should return a 400 Bad Request.")
    void createOption_returns400_whenInValidRequest() throws Exception{
        OptionRequestDto request = new OptionRequestDto("");
        mockMvc.perform(post("/api/events/{id}/options",EVENT_ID)
                        .header("X-User-Id",USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieving an Existing Option - 200 OK must be returned")
    void getOption_returns200_whenOptionExist() throws Exception{
        // Act
        when(optionService.getOption("1")).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(get("/api/options/{id}",OPTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

    }

    @Test
    @DisplayName("Retrieving up a non-existent option - should return 404 Not Found.")
    void getOptions_returns404_whenOptionNotExist() throws Exception{
        when(optionService.getOption(OPTION_ID)).thenThrow(new OptionNotFoundException("Option Not Found"));
        mockMvc.perform(get("/api/options/{id}",OPTION_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Option Deletion - should return 200 ok.")
    void deleteOption_returns200_whenOptionDeleted() throws  Exception{
        // Act
        when(optionService.deleteOption(OPTION_ID)).thenReturn(standardResponse);

        // Arrange
        mockMvc.perform(delete("/api/options/{id}",OPTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("A"));
    }

    @Test
    @DisplayName("Delete Non Existing Option - Must Return 404 NotFound")
    void deleteOption_returns404_whenOptionNotFound() throws Exception{
        // Act
        when(optionService.deleteOption(OPTION_ID)).thenThrow(new OptionNotFoundException("Option Not Found"));

        // Arrange
        mockMvc.perform(delete("/api/options/{id}",OPTION_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Partial Option Update - Should return 200 ok.")
    void patchOption_returns200_whenOptionPatched() throws Exception{
        // Arrange
        UpdateOptionRequestDto request = new UpdateOptionRequestDto("A");

        // Act
        when(optionService.patchOption(OPTION_ID,request)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(patch("/api/options/{id}",OPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OPTION_ID))
                .andExpect(jsonPath("$.content").value("A"));
    }
}
