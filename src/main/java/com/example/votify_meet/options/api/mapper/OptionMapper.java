package com.example.votify_meet.options.api.mapper;


import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.domain.model.Option;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OptionMapper {
    public OptionResponseDto toResponse(Option entity){
        return OptionResponseDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .voteCount(entity.getVoteCount())
                .build();
    }
    public Option toEntity(OptionRequestDto request, Event  event){
        return Option.builder().content(request.content()).event(event).build();
    }
    private String normalize(String value){
        if(value != null && value.trim().isEmpty()){
            return null;
        }
        return value;
    }
    public void update(UpdateOptionRequestDto request, Option entity){
        String content = normalize(request.content());
        if(content != null){
            entity.setContent(content);
        }
        entity.setUpdatedAt(Instant.now());
    }


}
