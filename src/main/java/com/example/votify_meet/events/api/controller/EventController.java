package com.example.votify_meet.events.api.controller;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Events", description = "Event Management APIs")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // todo - find the reason of yellow warnings then solve it

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody EventRequestDto eventRequestDto) {
        String userId = currentUser.getId();
        return eventService.createEvent(eventRequestDto, userId);
    }

    @GetMapping("/users/me/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDto> getEvents(@AuthenticationPrincipal Users currentUser) {
        return eventService.getUserEvents(currentUser.getId());

    }

    // todo - alttaki metotlar düzenlenecek, bunlar test ortamı için, her user aşağıdaki metotları çalıştırabilir
    // todo - CİDDİ AÇIK İLERİDE DÜZENLE TEST ORTAMI İÇİN KALSIN
    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEvent(@PathVariable(name = "id") String id) {
        return eventService.getEvent(id);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto deleteEvent(@PathVariable String id) {
        return eventService.deleteEvent(id);
    }

    @PatchMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(@PathVariable String id, @Valid @RequestBody UpdateEventRequestDto request) {
        return eventService.updateEvent(id, request);
    }

}
