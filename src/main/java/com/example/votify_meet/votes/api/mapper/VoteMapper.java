package com.example.votify_meet.votes.api.mapper;

import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.domain.model.Vote;
import org.springframework.stereotype.Component;

@Component
public class VoteMapper {

    public VoteResponseDto toResponse(Vote entity){
        return VoteResponseDto.builder()
                .id(entity.getId())
                .optionId(entity.getOption().getId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Vote toEntity(VoteRequestDto request, Option option){
        return Vote.builder().option(option).build();

    }
}
