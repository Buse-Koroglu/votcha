package com.example.votify_meet.votes.service;

import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.api.mapper.VoteMapper;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
import com.example.votify_meet.votes.domain.model.Vote;
import com.example.votify_meet.votes.domain.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final OptionRepository optionRepository;


    public VoteService(VoteRepository voteRepository, VoteMapper voteMapper, OptionRepository optionRepository) {
        this.voteRepository = voteRepository;
        this.voteMapper = voteMapper;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public VoteResponseDto createVote(VoteRequestDto request){
        Option option = optionRepository.findById(request.optionId()).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", request.optionId())));
        Vote vote = voteMapper.toEntity(request,option);
        return voteMapper.toResponse(voteRepository.saveAndFlush(vote));
    }

    public VoteResponseDto getVote(String id){
        return voteMapper.toResponse(voteRepository.findById(id).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id))));
    }


    public VoteResponseDto deleteVote(String id){
        Vote vote = voteRepository.findById(id).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        voteRepository.delete(vote);
        return voteMapper.toResponse(vote);
    }
    public VoteResponseDto updateVote(String id, VoteRequestDto request){
        Vote vote = voteRepository.findById(id).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        Option option = optionRepository.findById(request.optionId()).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));
        voteMapper.update(option, vote);
        return voteMapper.toResponse(voteRepository.saveAndFlush(vote));
    }

}
