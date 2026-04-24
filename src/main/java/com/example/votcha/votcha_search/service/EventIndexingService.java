package com.example.votcha.votcha_search.service;

import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;
import com.example.votcha.votcha_search.api.dto.response.EventSyncResponse;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.model.OptionDocument;
import com.example.votcha.votcha_search.domain.repository.EventElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventIndexingService {
    private final EventsRepo eventRepo;
    private final EventElasticRepository eventElasticRepository;

    @Transactional(readOnly=true)
    public EventSyncResponse syncAllEvents() {
        int pageSize = 500;
        int pageNumber = 0;
        long totalSynced = 0;
        long totalFound = eventRepo.count();

        Page<Event> eventPage;
        do {
            eventPage = eventRepo.findAll(PageRequest.of(pageNumber, pageSize));
            List<EventDocument> documents = eventPage.getContent().stream().map(this::mapToDocument).toList();

            if (!documents.isEmpty()) {
                eventElasticRepository.saveAll(documents);
                totalSynced += documents.size();
            }
            pageNumber++;
        } while (eventPage.hasNext());

        return EventSyncResponse.builder()
                .status("SUCCESS")
                .message("Event bulk synchronization completed successfully.")
                .totalDbCount(totalFound)
                .syncedCount(totalSynced)
                .timestamp(Instant.now())
                .build();

    }

    private EventDocument mapToDocument(Event event) {
        List<OptionDocument> optionDocuments = event.getOptions().stream().map(
                opt -> OptionDocument.builder()
                        .id(opt.getId())
                        .content(opt.getContent())
                        .voteCount(opt.getVoteCount())
                        .build()).toList();

        long totalVotes = optionDocuments.stream().mapToLong(OptionDocument::getVoteCount).sum();

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

    public void updateEventCurrentVoteCount(String eventId, Long currentCount,List<OptionSyncData> updatedOptions){
        eventElasticRepository.findById(eventId).ifPresent(doc -> {
            doc.setTotalVoteCount(currentCount);
            List<OptionDocument> optionDocuments = updatedOptions.stream().map(
                    opt -> OptionDocument.builder()
                            .id(opt.id())
                            .content(opt.content())
                            .voteCount(opt.voteCount())
                            .build()).toList();

            doc.setOptions(optionDocuments);
            eventElasticRepository.save(doc);
        });
    }
}
