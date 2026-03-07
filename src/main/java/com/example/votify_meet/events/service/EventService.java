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
import java.util.Map;
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

    public EventResponseDto getUserEvent(Users user, String eventId) {

        Event event = eventsRepo.findByIdAndCreator(eventId, user).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        List<Option> options = optionRepo.findAllByEvent_Id(eventId);
        return eventMapper.toResponse(event, options);
    }

    public EventResponseDto deleteUserEvent(Users user, String eventId) {
        Event event = eventsRepo.findByIdAndCreator(eventId, user).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        eventsRepo.delete(event);
        return eventMapper.toResponse(event, Collections.emptyList());
    }

    public EventResponseDto updateUserEvents(Users user, String eventId, UpdateEventRequestDto request){
        Event entity = eventsRepo.findByIdAndCreator(eventId, user).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        eventMapper.update(request, entity);
        Event updatedEvent = eventsRepo.save(entity);
        List<Option> options = optionRepo.findAllByEvent_Id(eventId);
        return eventMapper.toResponse(updatedEvent, options);
    }
    public List<EventResponseDto> getUserEvents(String userId) {
        usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User id %s not found", userId)));

        List<Event> userEvents = eventsRepo.findAllByCreatorId(userId)
                .orElse(Collections.emptyList());
        if(userEvents.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> eventIds = userEvents.stream()
                .map(Event::getId)
                .toList();

        List<Option> allOptions = optionRepo.findAllByEvent_IdIn(eventIds);
        Map<String, List<Option>> optionsByEventId = allOptions.stream()
                .collect(Collectors.groupingBy(Option::getEventId));

        return userEvents.stream()
                .map(event -> {
                    List<Option> eventOptions = optionsByEventId.getOrDefault(event.getId(), Collections.emptyList());
                    return eventMapper.toResponse(event, eventOptions);
                })
                .toList();
    }

}
