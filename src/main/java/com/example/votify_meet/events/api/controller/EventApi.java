package com.example.votify_meet.events.api.controller;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@Tag(name = "Events", description = "Event Management APIs")
public interface EventApi {
    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventResponseDto createEvent(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody EventRequestDto eventRequestDto);

    @GetMapping("/users/me/events")
    @ResponseStatus(HttpStatus.OK)
    List<EventResponseDto> getEvents(@AuthenticationPrincipal Users currentUser);

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventResponseDto getEvent(@AuthenticationPrincipal Users user, @PathVariable(name = "id") String id);

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventResponseDto deleteEvent(@AuthenticationPrincipal Users user, @PathVariable String id);

    @PatchMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventResponseDto updateEvent(@AuthenticationPrincipal Users user, @PathVariable String id, @Valid @RequestBody UpdateEventRequestDto request);
}
