package com.example.votify_meet.votes.api.mapper;

import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.domain.model.Vote;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class VoteMapper {

    public VoteResponseDto toResponse(Vote entity){
        return VoteResponseDto.builder()
                .id(entity.getId())
                .optionId(entity.getOption().getId())
                .voterId(entity.getVoter().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Vote toEntity(Option option, Users user){
        return Vote.builder().option(option).voter(user).build();
    }
    public void update(Option option, Vote entity){
        entity.setOption(option);
        entity.setUpdatedAt(Instant.now());
    }
}
