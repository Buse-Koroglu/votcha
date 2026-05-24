package com.example.votcha.options.api.dto;

import com.example.votcha.votes.api.dto.VoteResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OptionDetailResponseDto {
    private OptionResponseDto option;
    private List<VoteResponseDto> votes;
}
