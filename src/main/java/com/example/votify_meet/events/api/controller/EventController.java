package com.example.votify_meet.events.api.controller;

import com.example.votify_meet.common.logging.BusinessAction;
import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.users.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {
    private final EventService eventService;

    @BusinessAction(action = "EVENT_CREATED", domain = "EVENTS", logDetails = "'Title: ' + #eventRequestDto.title + ' | Options Count: ' + #eventRequestDto.options.size()")
    @Override
    public EventResponseDto createEvent(Users currentUser, EventRequestDto eventRequestDto) {
        String userId = currentUser.getId();
        return eventService.createEvent(eventRequestDto, userId);
    }

    @Override
    public List<EventResponseDto> getAllEvents(Users currentUser) {
        return eventService.getAll(currentUser);
    }

    @Override
    public List<EventResponseDto> getAllUserEvents(Users currentUser) {return eventService.getUserEvents(currentUser.getId());}

    @Override
    public EventResponseDto getEvent(Users user, String id) {
        return eventService.getUserEvent(user, id);
    }

    @BusinessAction(action = "EVENT_DELETED", domain = "EVENTS", logDetails = "'Event ID: ' + #id")
    @Override
    public EventResponseDto deleteEvent(Users user, String id) {
        return eventService.deleteUserEvent(user, id);
    }

    @BusinessAction(action = "EVENT_UPDATED", domain = "EVENTS", logDetails = "'Event ID: ' + #id")
    @Override
    public EventResponseDto updateEvent(Users user, String id, UpdateEventRequestDto request) {return eventService.updateUserEvents(user, id, request);}

}
