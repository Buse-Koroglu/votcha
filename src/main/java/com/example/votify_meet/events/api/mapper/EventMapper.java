package com.example.votify_meet.events.api.mapper;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.model.Status;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.stereotype.Component;

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
}
