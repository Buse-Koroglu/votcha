package com.example.votify_meet.events.service;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.api.mapper.EventMapper;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.repository.EventsRepo;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventsRepo eventsRepo;
    private final EventMapper eventMapper;
    private final UsersRepo  usersRepo;

    public EventService(EventsRepo eventsRepo, EventMapper eventMapper,
                        UsersRepo usersRepo) {
        this.eventsRepo = eventsRepo;
        this.eventMapper = eventMapper;
        this.usersRepo = usersRepo;
    }

    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto, String userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        return eventMapper.toResponse(eventsRepo.saveAndFlush(eventMapper.toEntity(eventRequestDto, user)));
    }

    public EventResponseDto getEvent(String eventId) {
        return eventMapper.toResponse(eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId))));
    }

    public EventResponseDto deleteEvent(String eventId) {
        Event event = eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        eventsRepo.deleteById(eventId);
        return eventMapper.toResponse(event);
    }

    public EventResponseDto updateEvent(String id,  UpdateEventRequestDto request) {
        Event entity = eventsRepo.findById(id).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", id)));
        eventMapper.update(request, entity);
        return eventMapper.toResponse(eventsRepo.save(entity));
    }
    public List<EventResponseDto> getUserEvents(String userId) {
        usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User id %s not found", userId)));
        return eventsRepo.findAllByCreatorId(userId)
                .stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

}
