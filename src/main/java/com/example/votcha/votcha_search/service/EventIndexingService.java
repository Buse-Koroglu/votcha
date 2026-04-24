package com.example.votcha.votcha_search.service;

import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;
import com.example.votcha.votcha_search.api.dto.response.EventSyncResponse;
import com.example.votcha.votcha_search.api.mapper.EventElasticMapper;
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
    private final EventElasticMapper eventMapper;

    @Transactional(readOnly=true)
    public EventSyncResponse syncAllEvents() {
        int pageSize = 500;
        int pageNumber = 0;
        long totalSynced = 0;
        long totalFound = eventRepo.count();

        Page<Event> eventPage;
        do {
            eventPage = eventRepo.findAll(PageRequest.of(pageNumber, pageSize));
            List<EventDocument> documents = eventPage.getContent().stream().map(
                    e -> {
                            List<OptionDocument> optionDocuments = e.getOptions().stream()
                                            .map(eventMapper::optionToOptionDocument).toList();
                            long totalVotes = optionDocuments.stream().mapToLong(OptionDocument::getVoteCount).sum();

                            return eventMapper.eventToEventDocument(e, totalVotes,  optionDocuments);
                    }
            ).toList();

            if (!documents.isEmpty()) {
                eventElasticRepository.saveAll(documents);
                totalSynced += documents.size();
            }
            pageNumber++;
        } while (eventPage.hasNext());

        return EventSyncResponse.builder()
                .timestamp(Instant.now())
                .status("SUCCESS")
                .totalDbCount(totalFound)
                .syncedCount(totalSynced)
                .message("Event bulk synchronization completed successfully.")
                .build();

    }

    public void updateEventCurrentVoteCount(String eventId, Long currentCount,List<OptionSyncData> updatedOptions){
        eventElasticRepository.findById(eventId).ifPresent(doc -> {
            doc.setTotalVoteCount(currentCount);
            List<OptionDocument> optionDocuments = updatedOptions.stream()
                        .map(eventMapper::optionSyncToOptionDocument).toList();

            doc.setOptions(optionDocuments);
            eventElasticRepository.save(doc);
        });
    }
}
