package com.example.votify_meet.controller;
import com.example.votify_meet.events.api.controller.EventController;
import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.api.mapper.EventMapper;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.EventType;
import com.example.votify_meet.events.domain.model.Status;
import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.users.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.Instant;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    @MockitoBean
    private EventService eventService;
    @MockitoBean
    private UsersService usersService;
    @MockitoBean
    private EventMapper eventMapper;

    private final String EVENT_ID = "1";
    private final String USER_ID = "1";
    private Instant deadline;
    private EventResponseDto standardResponse;

    @BeforeEach
    public void setup() {
        deadline =Instant.parse("2026-03-04T19:42:11.234Z");
        standardResponse = new EventResponseDto(
                EVENT_ID, "Meet Event","Today",Status.OPEN,
                EventType.STANDARD, null, null, deadline, USER_ID, null
        );
    }

    @Test
    @DisplayName("Successful Event Creation - Should return 201 Created and correct JSON.")
    void createEvent_returns201_whenValidRequest() throws Exception{
        // Arrange
        EventRequestDto request = new EventRequestDto("Meet Event","Today",deadline, EventType.STANDARD);

        // Act
        when(eventService.createEvent(any(EventRequestDto.class),anyString())).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EVENT_ID))
                .andExpect(jsonPath("$.title").value("Meet Event"))
                .andExpect(jsonPath("$.description").value("Today"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.type").value("STANDARD"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()))
                .andExpect(jsonPath("$.deadline").value(deadline.toString()))
                .andExpect(jsonPath("$.creatorId").value("1"))
                .andExpect(jsonPath("$.options").value(nullValue()));
    }

    @Test
    @DisplayName("Creating an Event with an Invalid Request (Null Deadline) - It should return a 400 Bad Request.")
    void createEvent_returns400_whenInvalidRequest() throws Exception{
        // Arrange
        EventRequestDto request = new EventRequestDto("Meet Event","Today",null, EventType.STANDARD); // deadline can not be null in the request so it will be invalid request

        // Act & Assert
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieve an Existing Event - It should return 200 OK and event data.")
    void getEvent_returns200_whenEventExist() throws Exception{
        // Act
        when(eventService.getEvent(EVENT_ID)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(get("/api/events/{id}",EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID))
                .andExpect(jsonPath("$.description").value("Today"))
                .andExpect(jsonPath("$.deadline").value(deadline.toString()));
    }

    @Test
    @DisplayName("Retrieving a non-existent event - it should return a 404 Not Found error.")
    void getEvent_returns404_whenEventNotExist() throws Exception{
        // Act
        when(eventService.getEvent(EVENT_ID)).thenThrow(new EventNotFoundException("Event Not Found"));

        // Assert
        mockMvc.perform(get("/api/events/{id}",EVENT_ID))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Event Deletion - Must return 200 OKs.")
    void deleteEvent_returns200_whenEventDeleted() throws Exception{
        // Act
        when(eventService.deleteEvent(EVENT_ID)).thenReturn(standardResponse);

        // Assert
        mockMvc.perform(delete("/api/events/{id}",EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID));
    }

    @Test
    @DisplayName("Non Existing Event Deletion - Must return 404 Not Found Error.")
    void deleteEvent_returns404_whenEventNotFound() throws Exception{
        // Act
        when(eventService.deleteEvent(EVENT_ID)).thenThrow(new EventNotFoundException("Event Not Found"));

        // Assert
        mockMvc.perform(delete("/api/events/{id}",EVENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Partial Update (Patch) - Should return 200 OK and updated data.")
    void patchEvent_returns200_whenEventPatched() throws Exception{
        // Arrange
        UpdateEventRequestDto request = new UpdateEventRequestDto("Meet Event","Today",deadline);

        // Act
        when(eventService.updateEvent(EVENT_ID,request)).thenReturn(standardResponse);

        // Asset
        mockMvc.perform(patch("/api/events/{id}",EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EVENT_ID))
                .andExpect(jsonPath("$.title").value("Meet Event"))
                .andExpect(jsonPath("$.description").value("Today"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.type").value("STANDARD"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()))
                .andExpect(jsonPath("$.deadline").value(deadline.toString()))
                .andExpect(jsonPath("$.creatorId").value("1"))
                .andExpect(jsonPath("$.options").value(nullValue()));
    }


    @Test
    @DisplayName("Invalid Patch Request - Should return 400 bad request.")
    void patchEvent_returns400_whenInvalidRequest() throws Exception{
        // Arrange
        UpdateEventRequestDto request = new UpdateEventRequestDto("","",null); // for the update deadline can be null but title and description invalid now.

        // Act & Assert
        mockMvc.perform(patch("/api/events/{id}",EVENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());
    }
}
