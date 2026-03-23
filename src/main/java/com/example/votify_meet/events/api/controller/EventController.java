package com.example.votify_meet.events.api.controller;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController implements EventApi {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    

    @Override
    public EventResponseDto createEvent(Users currentUser, EventRequestDto eventRequestDto) {
        String userId = currentUser.getId();
        return eventService.createEvent(eventRequestDto, userId);
    }

    @Override
    public List<EventResponseDto> getEvents(Users currentUser) {
        return eventService.getUserEvents(currentUser.getId());

    }

    @Override
    public EventResponseDto getEvent(Users user, String id) {
        return eventService.getUserEvent(user, id);
    }

    @Override
    public EventResponseDto deleteEvent(Users user, String id) {
        return eventService.deleteUserEvent(user, id);
    }

    @Override
    public EventResponseDto updateEvent(Users user, String id, UpdateEventRequestDto request) {
        return eventService.updateUserEvents(user, id, request);
    }

}
