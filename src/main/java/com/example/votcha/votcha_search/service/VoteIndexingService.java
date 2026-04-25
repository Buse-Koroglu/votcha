package com.example.votcha.votcha_search.service;


import com.example.votcha.votcha_search.api.dto.response.VoteSyncResponse;
import com.example.votcha.votcha_search.api.mapper.VoteElasticMapper;
import com.example.votcha.votcha_search.domain.model.VoteDocument;
import com.example.votcha.votcha_search.domain.repository.VoteElasticRepository;
import com.example.votcha.votes.domain.model.Vote;
import com.example.votcha.votes.domain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
@Service
@RequiredArgsConstructor
public class VoteIndexingService {
    private final VoteRepository voteRepo;
    private final VoteElasticRepository voteElasticRepository;
    private final VoteElasticMapper voteElasticMapper;

    @Transactional(readOnly=true)
    public VoteSyncResponse syncAllVotes() {
        int pageSize = 1000;
        int pageNumber = 0;
        long totalSynced = 0;
        long totalFound = voteRepo.count();

        Page<Vote> votePage;
        do {
            votePage = voteRepo.findAll(PageRequest.of(pageNumber, pageSize));
            List<VoteDocument> documents = votePage.getContent().stream()
                    .map(voteElasticMapper::voteToVoteDocument).toList();
            if(!documents.isEmpty()) {
                voteElasticRepository.saveAll(documents);
                totalSynced += documents.size();
            }
            pageNumber++;
        }while(votePage.hasNext());

        return VoteSyncResponse.builder()
                .timestamp(Instant.now())
                .status("SUCCESS")
                .syncedCount(totalSynced)
                .totalDbCount(totalFound)
                .message("Bulk synchronization completed successfully.")
                .build();
    }

}
