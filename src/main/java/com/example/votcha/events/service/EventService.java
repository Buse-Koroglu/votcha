package com.example.votcha.events.service;

import com.example.votcha.common.exception.AppAccessDeniedException;
import com.example.votcha.common.logging.SystemActionLogger;
import com.example.votcha.events.api.dto.EventDetailResponseDto;
import com.example.votcha.events.api.dto.EventRequestDto;
import com.example.votcha.events.api.dto.EventResponseDto;
import com.example.votcha.events.api.dto.UpdateEventRequestDto;
import com.example.votcha.events.api.mapper.EventMapper;
import com.example.votcha.events.domain.exception.EventNotFoundException;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.model.EventType;
import com.example.votcha.events.domain.model.Status;
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.users.api.dto.CreatorResponse;
import com.example.votcha.users.api.mapper.UsersMapper;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.service.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventsRepo eventsRepo;
    private final EventMapper eventMapper;
    private final UsersMapper usersMapper;
    private final UsersRepo  usersRepo;
    private final OptionRepository optionRepo;
    private final SystemActionLogger systemActionLogger;
    private final VoteService  voteService;


    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto, String userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        Event savedEvent = eventsRepo.saveAndFlush(eventMapper.toEntity(eventRequestDto, user));
        return eventMapper.toResponse(savedEvent, savedEvent.getOptions());
    }

    public EventResponseDto getUserEvent(Users user, String eventId) {
        Event event = eventsRepo.findByIdAndCreator(eventId, user).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        List<Option> options = optionRepo.findAllByEvent_Id(eventId);
        return eventMapper.toResponse(event, options);
    }

    ///  Retrieves the Events Details (Options with Voters)
    @Transactional
    public EventDetailResponseDto getEventDetail(Users user, String eventId) {
        Event event = eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s", eventId)));

        if(!user.getId().equals(event.getCreator().getId())) {
            throw new AppAccessDeniedException("You are not allowed to access this event.");
        }

        /// Groups the option and its voters
        List<VoteResponseDto> allVotes = voteService.getEventVotes(event.getId());
        Map<String, List<VoteResponseDto>> votesByOptionId = allVotes.stream()
                .collect(Collectors.groupingBy(VoteResponseDto::optionId));

        return eventMapper.toDetailResponse(event, votesByOptionId);
    }

    @Transactional
    public List<EventResponseDto> getAll(Users user){
        usersRepo.findById(user.getId()).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", user.getId())));
        return eventsRepo.findAll().stream().map(event ->  eventMapper.toResponse(event, event.getOptions())).collect(Collectors.toList());
    }

    public EventResponseDto deleteUserEvent(Users user, String eventId) {
        Event event = eventsRepo.findByIdAndCreator(eventId, user).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        eventsRepo.delete(event);
        return eventMapper.toResponse(event, Collections.emptyList());
    }
    @Transactional
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

    public CreatorResponse getCreatorSummary(String creatorId) {
        Users creator = usersRepo.findById(creatorId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", creatorId)));
        return usersMapper.toCreatorResponse(creator);
    }

    @Transactional
    public void revealExpiredSurpriseEvents(){
        Instant now = Instant.now();
        int counts = eventsRepo.setStandardExpiredSurprisedEvents(now);

        if(counts == 0) {
            return;
        }

        systemActionLogger.execute(
                "EVENTS",
                "SURPRISE_EVENTS_REVEALED",
                "Revealed count: "+ counts,
                "SYSTEM_SCHEDULER",
                () -> {}
        );
    }

    @Transactional
    public  void closeExpiredEvents() {
        Instant now = Instant.now();
        int counts = eventsRepo.closeExpiredEvents(now);
        if(counts == 0) {
            return;
        }

        systemActionLogger.execute(
                "EVENTS",
                "EXPIRED_EVENTS_CLOSED",
                "Closed " + counts + " expired events",
                "SYSTEM_SCHEDULER",
                () -> {}
        );

    }

}
