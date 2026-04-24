package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.events.domain.model.Event;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;
import com.example.votcha.votcha_search.api.dto.event.EventCreatedSyncEvent;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.model.OptionDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventElasticMapper {

    public EventDocument eventToEventDocument(Event event, long totalVotes, List<OptionDocument> optionDocuments) {
        return EventDocument.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .deadline(event.getDeadline())
                .createdAt(event.getCreatedAt())
                .type(event.getType().name())
                .status(event.getStatus().name())
                .creatorName(event.getCreator().getFirstName() + " " + event.getCreator().getLastName())
                .creatorEmail(event.getCreator().getEmail())
                .totalVoteCount(totalVotes)
                .options(optionDocuments)
                .build();
    }
    public EventCreatedSyncEvent eventToEventCreatedSyncEvent(Event event, long totalVotes) {
        return EventCreatedSyncEvent.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .deadline(event.getDeadline())
                .createdAt(event.getCreatedAt())
                .type(event.getType().name())
                .status(event.getStatus().name())
                .creatorName(event.getCreator().getFirstName() + " " + event.getCreator().getLastName())
                .creatorEmail(event.getCreator().getEmail())
                .totalVoteCount(totalVotes)
                .options(
                        event.getOptions().stream()
                                .map(this::optionToOptionSyncData)
                                .toList()
                ).build();
    }
    public EventDocument eventCreatedSyncToEventDocument(EventCreatedSyncEvent event, List<OptionDocument> optionDocuments) {
        return EventDocument.builder()
                .id(event.id())
                .title(event.title())
                .description(event.description())
                .deadline(event.deadline())
                .createdAt(event.createdAt())
                .type(event.type())
                .status(event.status())
                .creatorName(event.creatorName())
                .creatorEmail(event.creatorEmail())
                .totalVoteCount(event.totalVoteCount() != null ? event.totalVoteCount(): 0L)
                .options(optionDocuments)
                .build();
    }
    public OptionSyncData optionToOptionSyncData(Option option) {
        return OptionSyncData.builder()
                .voteCount(option.getVoteCount())
                .content(option.getContent())
                .id(option.getId())
                .build();
    }
    public OptionDocument optionToOptionDocument(Option option){
        return OptionDocument.builder()
                .id(option.getId())
                .content(option.getContent())
                .voteCount(option.getVoteCount())
                .build();
    }
    public OptionDocument optionSyncToOptionDocument(OptionSyncData opt){
        return OptionDocument.builder()
                .id(opt.id())
                .content(opt.content())
                .voteCount(opt.voteCount())
                .build();
    }
}
