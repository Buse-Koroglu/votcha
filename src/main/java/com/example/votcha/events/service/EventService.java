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
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.redis.vote.service.VoteRedisService;
import com.example.votcha.users.api.dto.CreatorResponse;
import com.example.votcha.users.api.mapper.UsersMapper;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votcha_search.api.dto.event.EventDeletedSyncEvent;
import com.example.votcha.votcha_search.api.mapper.EventElasticMapper;
import com.example.votcha.votcha_search.service.VoteIndexingService;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.service.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventsRepo eventsRepo;
    private final UsersRepo  usersRepo;
    private final OptionRepository optionRepo;

    private final EventMapper eventMapper;
    private final UsersMapper usersMapper;
    private final EventElasticMapper eventElasticMapper;

    private final SystemActionLogger systemActionLogger;
    private final VoteService  voteService;
    private final ApplicationEventPublisher eventPublisher;

    private final VoteIndexingService  voteIndexingService;
    private final VoteRedisService redisService;

    public void publishEventUpdate(Event event) {
        long currentTotalVotes = event.getTotalVoteCount();

        eventPublisher.publishEvent(eventElasticMapper.eventToEventCreatedSyncEvent(event, currentTotalVotes));
    }


    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto, String userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        Event savedEvent = eventsRepo.saveAndFlush(eventMapper.toEntity(eventRequestDto, user));
        publishEventUpdate(savedEvent);
        return eventMapper.toResponse(savedEvent, savedEvent.getOptions());
    }

    public EventResponseDto getUserEvent(Users user, String eventId) {
        Event event = getEventIfExist(eventId);
        List<Option> options = optionRepo.findAllByEvent_Id(eventId);
        return eventMapper.toResponse(event, options);
    }

    ///  Retrieves the Events Details (Options with Voters)
    @Transactional
    public EventDetailResponseDto getEventDetail(Users user, String eventId) {
        Event event = getEventIfExist(eventId);

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

    @Transactional
    public EventResponseDto deleteUserEvent(Users user, String eventId) {
        Event event = getUserEventIfExist(eventId, user);
        eventsRepo.delete(event);
        redisService.deleteEventFully(eventId);
        EventDeletedSyncEvent deletedSyncEvent= new EventDeletedSyncEvent(eventId);
        eventPublisher.publishEvent(deletedSyncEvent);
        return eventMapper.toResponse(event, Collections.emptyList());
    }
    @Transactional
    public EventResponseDto updateUserEvents(Users user, String eventId, UpdateEventRequestDto request){
        Event entity = getUserEventIfExist(eventId, user);
        eventMapper.update(request, entity);
        Event updatedEvent = eventsRepo.save(entity);
        publishEventUpdate(updatedEvent);
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

    private Event getEventIfExist(String eventId) {
        return eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
    }
    private Event getUserEventIfExist(String eventId, Users creator) {
        return eventsRepo.findByIdAndCreator(eventId, creator).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
    }

    @Transactional
    public void revealExpiredSurpriseEvents(){
        Instant now = Instant.now();
        int counts = eventsRepo.setStandardExpiredSurprisedEvents(now);

        if(counts == 0) {
            return;
        }

        sendToLoggerExecute("SURPRISE_EVENTS_REVEALED", "Revealed count: "+ counts, () -> {});
    }

    @Transactional
    public  void closeExpiredEvents() {

        Instant now = Instant.now();

        // Get expired event ids
        List<String> expiredIds = eventsRepo.findExpiredEventIds(now);
        if(expiredIds.isEmpty()) return;

        // Set Status as 'CLOSED' the expired events then send the log.
        int counts = eventsRepo.closeExpiredEvents(now);
        sendToLoggerExecute("EXPIRED_EVENTS_CLOSED", "Closed " + counts + " expired events", () -> {});

        // Retrieve all the events using the ids
        List<Event> closedEvents = eventsRepo.findAllByIdIn(expiredIds);

        for(Event event: closedEvents){
            // Marks the winner option
            Optional<Option> winnerOption = determineAndSetWinner(event);
            publishEventUpdate(event);
            winnerOption.ifPresent(
                    option -> voteIndexingService.markWinnerVotesInElastic(option.getId())
            );
        }

    }
    private void sendToLoggerExecute(String action, String details,  Runnable task){
        systemActionLogger.execute(
                "EVENTS",
                action,
                details,
                "SYSTEM_SCHEDULER",
                task
        );
    }
    // Set to true the most voted option
    public Optional<Option> determineAndSetWinner(Event event){
        int maxVotes = event.getOptions().stream()
                .mapToInt(
                        Option::getVoteCount
                )
                .max()
                .orElse(0);

        if(maxVotes <= 0) return Optional.empty();

        Optional<Option> winner = event.getOptions().stream()
                .filter(
                        o -> o.getVoteCount() == maxVotes
                ).findFirst();
        winner.ifPresent(o -> o.setWinner(true));

        return winner;
    }

}
