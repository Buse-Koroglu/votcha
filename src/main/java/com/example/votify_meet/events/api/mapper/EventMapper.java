package com.example.votify_meet.events.api.mapper;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.dto.UpdateEventRequestDto;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.model.Status;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EventMapper {
    public EventResponseDto toResponse(Event entity) {
        return EventResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .deadline(entity.getDeadline())
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .creatorId(entity.getCreator().getId())
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
