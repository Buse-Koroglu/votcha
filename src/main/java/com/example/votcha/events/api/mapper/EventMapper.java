package com.example.votcha.events.api.mapper;

import com.example.votcha.events.api.dto.EventDetailResponseDto;
import com.example.votcha.events.api.dto.EventRequestDto;
import com.example.votcha.events.api.dto.EventResponseDto;
import com.example.votcha.events.api.dto.UpdateEventRequestDto;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.model.EventType;
import com.example.votcha.events.domain.model.Status;
import com.example.votcha.options.api.dto.OptionDetailResponseDto;
import com.example.votcha.options.api.dto.OptionResponseDto;
import com.example.votcha.options.api.mapper.OptionMapper;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.domain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final OptionMapper optionMapper;
    private final VoteRepository voteRepository;

    public EventResponseDto toResponse(Event entity, List<Option> options) {
        List<OptionResponseDto> optionDtos = options.stream()
                .map(optionMapper::toResponse)
                .toList();
        String desc = entity.getDescription();
        if(entity.getType().equals(EventType.SURPRISED)){
            desc = null;
        }
        return EventResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(desc)
                .deadline(entity.getDeadline())
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .creatorId(entity.getCreator().getId())
                .options(optionDtos)
                .build();
    }

    public EventDetailResponseDto toDetailResponse(Event entity, Map<String, List<VoteResponseDto>> votesByOptionId) {

        /// Finds the options with the votes of option
        List<OptionDetailResponseDto> optionDtos = entity.getOptions().stream()
                .map(option -> {
                    List<VoteResponseDto> optionVotes = votesByOptionId.getOrDefault(
                            option.getId(),
                            Collections.emptyList()
                    );
                    ///  Converts option and votes to OptionDetailResponse
                    return optionMapper.toDetailResponse(option, optionVotes);
                })
                .toList();
        /// Make the description private if it is SURPRISED
        String desc = isSurprised(entity) ? null : entity.getDescription();

        return EventDetailResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(desc)
                .deadline(entity.getDeadline())
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .creatorId(entity.getCreator().getId())
                .options(optionDtos)
                .build();
    }

    public Event toEntity(EventRequestDto request, Users user) {
        /// Create the event with an empty option
        Event event = Event.builder()
                .title(request.title())
                .status(Status.OPEN)
                .description(request.description())
                .deadline(request.deadline())
                .type(request.eventType())
                .creator(user)
                .build();

        /// Create the options
        List<Option> eventOptions = request.options().stream()
                .map(optDto -> Option.builder()
                        .content(optDto.content())
                        .build())
                .toList();
        /// Set the options to the related event then return
        event.addOptions(eventOptions);
        return event;
    }

    /// Trim the blanks if a user enters spaces
    private String normalize(String value){
        if(value != null && value.trim().isEmpty()){
            return null;
        }
        return value;
    }

    public void update(UpdateEventRequestDto request, Event entity) {
        String title = normalize(request.title());
        String description = normalize(request.description());

        if(title != null){
            entity.setTitle(title);
        }
        if(description != null){
            entity.setDescription(description);
        }
        if(request.deadline() != null) {
            entity.setDeadline(request.deadline());
        }

        entity.setUpdatedAt(Instant.now());

    }

    private boolean isSurprised(Event event){
        return event.getType().equals(EventType.SURPRISED);
    }
}
