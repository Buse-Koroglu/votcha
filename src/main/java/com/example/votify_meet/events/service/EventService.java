package com.example.votify_meet.events.service;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.api.mapper.EventMapper;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.repository.EventsRepo;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventsRepo eventsRepo;
    private final EventMapper eventMapper;
    private final UsersRepo  usersRepo;
    private final OptionRepository optionRepo;

    public EventService(EventsRepo eventsRepo, EventMapper eventMapper,
                        UsersRepo usersRepo, OptionRepository optionRepo) {
        this.eventsRepo = eventsRepo;
        this.eventMapper = eventMapper;
        this.usersRepo = usersRepo;
        this.optionRepo = optionRepo;
    }

    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto, String userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        return eventMapper.toResponse(eventsRepo.saveAndFlush(eventMapper.toEntity(eventRequestDto, user)), Collections.emptyList());
    }

    public EventResponseDto getEvent(String eventId) {
        List<Option> options = optionRepo.findAllByEventId(eventId);
        return eventMapper.toResponse(eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId))), options);
    }

    public EventResponseDto deleteEvent(String eventId) {
        Event event = eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        eventsRepo.deleteById(eventId);
        return eventMapper.toResponse(event, Collections.emptyList());
    }

    public EventResponseDto updateEvent(String id,  UpdateEventRequestDto request) {
        Event entity = eventsRepo.findById(id).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", id)));
        eventMapper.update(request, entity);
        Event updatedEvent = eventsRepo.save(entity);
        List<Option> options = optionRepo.findAllByEventId(id);
        return eventMapper.toResponse(updatedEvent, options);
    }
    public List<EventResponseDto> getUserEvents(String userId) {
        usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User id %s not found", userId)));
        return eventsRepo.findAllByCreatorId(userId)
                .stream()
                .map(event -> eventMapper.toResponse(event, Collections.emptyList()))
                .collect(Collectors.toList());
    }

}
