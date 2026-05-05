package com.example.votcha.votes.service;

import com.example.votcha.events.domain.exception.EventDeadlinePassedException;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.options.domain.exception.OptionNotFoundException;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.redis.vote.dto.VoteRedisDto;
import com.example.votcha.redis.vote.service.VoteRedisService;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;
import com.example.votcha.votcha_search.api.dto.event.VoteCountUpdatedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.VoteDeletedSyncEvent;
import com.example.votcha.votcha_search.api.mapper.OptionElasticMapper;
import com.example.votcha.votcha_search.api.mapper.VoteElasticMapper;
import com.example.votcha.votes.api.dto.VoteRequestDto;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.api.mapper.VoteMapper;
import com.example.votcha.votes.domain.exception.AlreadyVotedException;
import com.example.votcha.votes.domain.exception.VoteNotFoundException;
import com.example.votcha.votes.domain.model.Vote;
import com.example.votcha.votes.domain.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final UsersRepo  usersRepo;

    private final OptionElasticMapper  optionElasticMapper;
    private final VoteMapper voteMapper;

    private final ApplicationEventPublisher eventPublisher;
    private final VoteElasticMapper voteElasticMapper;
    private final VoteRedisService voteRedisService;


    @Transactional
    public VoteResponseDto createVote(VoteRequestDto request, String userId){
        Option option = optionRepository.findById(request.optionId()).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", request.optionId())));

        validateEventNotExpired(option.getEvent());

        boolean alreadyVoted = voteRepository.existsByVoterIdAndOption_Event_Id(userId,option.getEvent().getId());
        if(alreadyVoted){
            throw new AlreadyVotedException("User can not vote more than one time.");
        }

        Users voter = usersRepo.findById(userId).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        Vote savedVote = voteRepository.saveAndFlush(voteMapper.toEntity(option, voter));

        // redis vote create
        voteRedisService.createVote(
                userId,
                option.getEvent().getId(),
                option.getId(),
                savedVote.getId()
        );

        publishVoteUpdateEvent(option.getEvent().getId());
        eventPublisher.publishEvent(voteElasticMapper.voteToVoteCreatedSyncEvent(savedVote));

        return voteMapper.toResponse(savedVote);
    }

    public VoteResponseDto getUserVote(Users user, String id){
        return voteMapper.toResponse(voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id))));
    }

    public List<VoteResponseDto> getEventVotes(String eventId){
        return voteRepository.findAllByOption_Event_Id(eventId).orElse(Collections.emptyList()).stream().map(voteMapper::toResponse).toList();
    }

    @Transactional
    public VoteResponseDto deleteUserVote(Users user, String id){
        Vote vote = voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        String eventId = vote.getOption().getEvent().getId();
        String voteId = vote.getId();
        voteRepository.delete(vote);
        voteRepository.flush();
        // redis delete vote
        voteRedisService.deleteVote(user.getId(), eventId);

        publishVoteUpdateEvent(eventId);
        eventPublisher.publishEvent(new VoteDeletedSyncEvent(voteId));

        return voteMapper.toResponse(vote);
    }
    @Transactional
    public VoteResponseDto  updateVote(Users user, String id, VoteRequestDto request){
        Vote vote = voteRepository.findByIdAndVoter(id, user).orElseThrow( () -> new VoteNotFoundException(String.format("Vote with id %s not found", id)));
        Option option = optionRepository.findById(request.optionId()).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));

        validateEventNotExpired(option.getEvent());

        voteMapper.update(option, vote);
        Vote updatedVote = voteRepository.saveAndFlush(vote);
        // redis update vote
        voteRedisService.updateVote(
                user.getId(),
                option.getEvent().getId(),
                option.getId(),
                id
        );

        publishVoteUpdateEvent(option.getEvent().getId());
        eventPublisher.publishEvent(voteElasticMapper.voteToVoteCreatedSyncEvent(updatedVote));

        return voteMapper.toResponse(updatedVote);
    }

    public VoteResponseDto getUserVoteForEvent(String userId, String eventId) {
        // redis get vote
        voteRedisService.getUserVote(userId,eventId);

        VoteRedisDto redisVote = voteRedisService.getUserVote(userId, eventId);

        if (redisVote != null) {
            return VoteResponseDto.builder()
                    .id(redisVote.getVoteId())
                    .optionId(redisVote.getOptionId())
                    .voterId(redisVote.getUserId())
                    .build();
        }
        // fallback DB
        return voteRepository
                .findByVoter_IdAndOption_Event_Id(userId, eventId)
                .map(voteMapper::toResponse)
                .orElse(null);
    }

    private void publishVoteUpdateEvent(String eventId) {
        long newTotalVoteCount = getTotalVoteCountForEvent(eventId);

        List<OptionSyncData> updatedOptions = optionRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(optionElasticMapper::optionToOptionSyncData)
                .toList();

        eventPublisher.publishEvent(new VoteCountUpdatedSyncEvent(eventId, newTotalVoteCount, updatedOptions));
    }
    private void validateEventNotExpired(Event event){
        if(event.isExpired()){
            throw new EventDeadlinePassedException(String.format("Event with id %s is expired", event.getId()));
        }
    }

    private long getTotalVoteCountForEvent(String eventId) {
        return voteRedisService.getTotalVoteCount(eventId); // O(1) we are getting total vote count for event from redis not using COUNT(*) in db.
        // return voteRepository.countByOption_Event_Id(eventId);
    }
}
