package com.example.votcha.votes.service;

import com.example.votcha.events.domain.exception.EventDeadlinePassedException;
import com.example.votcha.options.domain.exception.OptionNotFoundException;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votes.api.dto.VoteRequestDto;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.api.mapper.VoteMapper;
import com.example.votcha.votes.domain.exception.AlreadyVotedException;
import com.example.votcha.votes.domain.exception.VoteNotFoundException;
import com.example.votcha.votes.domain.model.Vote;
import com.example.votcha.votes.domain.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final OptionRepository optionRepository;
    private final UsersRepo  usersRepo;


    @Transactional
    public VoteResponseDto createVote(VoteRequestDto request, String userId){
        Option option = optionRepository.findById(request.optionId()).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", request.optionId())));

        if(option.getEvent().isExpired()){
            throw new EventDeadlinePassedException(String.format("Option with id %s is expired", request.optionId()));
        }

        boolean alreadyVoted = voteRepository.existsByVoterIdAndOption_Event_Id(userId,option.getEvent().getId());
        if(alreadyVoted){
            throw new AlreadyVotedException("User can not vote more than one time.");
        }

        Users voter = usersRepo.findById(userId).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        Vote vote = voteMapper.toEntity(option, voter);
        return voteMapper.toResponse(voteRepository.saveAndFlush(vote));
    }

    public VoteResponseDto getUserVote(Users user, String id){
        return voteMapper.toResponse(voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id))));
    }

    public List<VoteResponseDto> getEventVotes(String eventId){
        return voteRepository.findAllByOption_Event_Id(eventId).orElse(Collections.emptyList()).stream().map(voteMapper::toResponse).toList();
    }


    public VoteResponseDto deleteUserVote(Users user, String id){
        Vote vote = voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        voteRepository.delete(vote);
        return voteMapper.toResponse(vote);
    }
    @Transactional
    public VoteResponseDto  updateVote(Users user, String id, VoteRequestDto request){
        Vote vote = voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        Option option = optionRepository.findById(request.optionId()).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));

        if(option.getEvent().isExpired()){
            throw new EventDeadlinePassedException(String.format("Option with id %s is expired", request.optionId()));
        }

        voteMapper.update(option, vote);
        return voteMapper.toResponse(voteRepository.saveAndFlush(vote));
    }

    public VoteResponseDto getUserVoteForEvent(String userId, String eventId) {
        return voteRepository
                .findByVoter_IdAndOption_Event_Id(userId, eventId)
                .map(voteMapper::toResponse)
                .orElse(null);
    }
}
