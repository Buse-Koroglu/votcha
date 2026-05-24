package com.example.votcha.options.api.mapper;


import com.example.votcha.events.domain.model.Event;
import com.example.votcha.options.api.dto.OptionDetailResponseDto;
import com.example.votcha.options.api.dto.OptionRequestDto;
import com.example.votcha.options.api.dto.OptionResponseDto;
import com.example.votcha.options.api.dto.UpdateOptionRequestDto;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

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

    /// Returns the Option with its Vote's
    public OptionDetailResponseDto toDetailResponse(Option entity, List<VoteResponseDto> votesResponse){
        OptionResponseDto response = OptionResponseDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .voteCount(entity.getVoteCount())
                .build();

        return OptionDetailResponseDto.builder()
                .option(response)
                .votes(votesResponse).build();
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
