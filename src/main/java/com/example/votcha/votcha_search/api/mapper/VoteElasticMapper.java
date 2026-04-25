package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.votcha_search.api.dto.event.VoteCreatedSyncEvent;
import com.example.votcha.votcha_search.domain.model.VoteDocument;
import com.example.votcha.votes.domain.model.Vote;
import org.springframework.stereotype.Component;

@Component
public class VoteElasticMapper {
    public VoteDocument voteToVoteDocument(Vote vote){
        return VoteDocument.builder()
                .id(vote.getId())
                .optionId(vote.getOption().getId())
                .optionContent(vote.getOption().getContent())
                .voterId(vote.getVoter().getId())
                .voterFullName(vote.getVoter().getFirstName() + " " + vote.getVoter().getLastName())
                .eventId(vote.getEventId())
                .eventTitle(vote.getOption().getEvent().getTitle())
                .createdAt(vote.getCreatedAt())
                .isWinner(vote.getOption().isWinner())
                .build();
    }

    public VoteDocument voteCreatedSyncToVoteDocument(VoteCreatedSyncEvent syncEvent){
        return VoteDocument.builder()
                .id(syncEvent.id())
                .optionId(syncEvent.optionId())
                .optionContent(syncEvent.optionContent())
                .voterId(syncEvent.voterId())
                .voterFullName(syncEvent.voterFullName())
                .eventId(syncEvent.eventId())
                .eventTitle(syncEvent.eventTitle())
                .createdAt(syncEvent.createdAt())
                .isWinner(syncEvent.isWinner())
                .build();
    }

    public VoteCreatedSyncEvent voteToVoteCreatedSyncEvent(Vote vote) {
        return new VoteCreatedSyncEvent(
                vote.getId(),
                vote.getOption().getId(),
                vote.getOption().getContent(),
                vote.getVoter().getId(),
                vote.getVoter().getFirstName() + " " + vote.getVoter().getLastName(),
                vote.getEventId(),
                vote.getOption().getEvent().getTitle(),
                vote.getCreatedAt(),
                vote.getOption().isWinner()
        );
    }
}
