package com.example.votify_meet.events.api.mapper;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.model.EventType;
import com.example.votify_meet.events.domain.model.Status;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.users.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final OptionMapper optionMapper;

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

    public Event toEntity(EventRequestDto request, Users user) {
        return Event.builder()
                .title(request.title())
                .status(Status.OPEN)
                .description(request.description())
                .deadline(request.deadline())
                .type(request.eventType())
                .creator(user)
                .build();
    }

    // Trim the blanks if a user enters spaces
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
        entity.setUpdatedAt(Instant.now());

    }
}
