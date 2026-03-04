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

    @Test
    void createEvent_returns201_whenValidRequest() throws Exception{
        Instant deadline = Instant.parse("2026-03-04T19:42:11.234Z");
        EventRequestDto request = new EventRequestDto("Meet Event","Today",deadline, EventType.STANDARD);
        EventResponseDto response = new EventResponseDto("1","Meet Event","Today", Status.OPEN,EventType.STANDARD,null,null,deadline,"1",null);
        when(eventService.createEvent(any(EventRequestDto.class),anyString())).thenReturn(response);

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
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
    void createEvent_returns400_whenInvalidRequest() throws Exception{
        EventRequestDto request = new EventRequestDto("Meet Event","Today",null, EventType.STANDARD); // deadline can not be null in the request so it will be invalid request

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEvent_returns200_whenEventExist() throws Exception{
        Instant deadline = Instant.parse("2026-03-04T19:42:11.234Z");
        EventResponseDto response = new EventResponseDto("1","Meet Event","Today", Status.OPEN,EventType.STANDARD,null,null,deadline,"1",null);
        when(eventService.getEvent("1")).thenReturn(response);
        mockMvc.perform(get("/api/events/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("Today"))
                .andExpect(jsonPath("$.deadline").value(deadline.toString()));

    }

    @Test
    void getEvent_returns404_whenEventNotExist() throws Exception{
        when(eventService.getEvent("1")).thenThrow(new EventNotFoundException("Event Not Found"));
        mockMvc.perform(get("/api/events/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEvent_returns200_whenEventDeleted() throws Exception{
        Instant deadline = Instant.parse("2026-03-04T19:42:11.234Z");
        EventResponseDto response = new EventResponseDto("1","Meet Event","Today", Status.OPEN,EventType.STANDARD,null,null,deadline,"1",null);
        when(eventService.deleteEvent("1")).thenReturn(response);
        mockMvc.perform(delete("/api/events/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deleteEvent_returns404_whenEventNotFound() throws Exception{
        when(eventService.deleteEvent("1")).thenThrow(new EventNotFoundException("Event Not Found"));
        mockMvc.perform(delete("/api/events/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchEvent_returns200_whenEventPatched() throws Exception{
        Instant deadline = Instant.parse("2026-03-04T19:42:11.234Z");
        UpdateEventRequestDto request = new UpdateEventRequestDto("Meet Event","Today",deadline);
        EventResponseDto response = new EventResponseDto("1","Meet Event","Today", Status.OPEN,EventType.STANDARD,null,null,deadline,"1",null);
        when(eventService.updateEvent("1",request)).thenReturn(response);
        mockMvc.perform(patch("/api/events/{id}","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
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
    void patchEvent_returns400_whenInvalidRequest() throws Exception{
        UpdateEventRequestDto request = new UpdateEventRequestDto("","",null); // for the update deadline can be null but title and description invalid now.
        mockMvc.perform(patch("/api/events/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());
    }

}
