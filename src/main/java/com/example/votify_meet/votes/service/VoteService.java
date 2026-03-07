package com.example.votify_meet.votes.service;

import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.api.mapper.VoteMapper;
import com.example.votify_meet.votes.domain.exception.AlreadyVotedException;
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
    private UsersRepo  usersRepo;


    public VoteService(VoteRepository voteRepository, VoteMapper voteMapper,
                       OptionRepository optionRepository, UsersRepo usersRepo) {
        this.voteRepository = voteRepository;
        this.voteMapper = voteMapper;
        this.optionRepository = optionRepository;
        this.usersRepo = usersRepo;
    }

    @Transactional
    public VoteResponseDto createVote(VoteRequestDto request, String userId){
        Option option = optionRepository.findById(request.optionId()).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", request.optionId())));
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


    public VoteResponseDto deleteUserVote(Users user, String id){
        Vote vote = voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
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
