package com.example.votcha.votcha_search.service;


import com.example.votcha.common.logging.SystemActionLogger;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;



import com.example.votcha.votcha_search.api.dto.response.VoteSyncResponse;
import com.example.votcha.votcha_search.api.mapper.VoteElasticMapper;
import com.example.votcha.votcha_search.domain.model.VoteDocument;
import com.example.votcha.votcha_search.domain.repository.VoteElasticRepository;
import com.example.votcha.votes.domain.model.Vote;
import com.example.votcha.votes.domain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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

    private final ElasticsearchOperations elasticsearchOperations;
    private final SystemActionLogger systemActionLogger;

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


    /**
     * Updating many votes ins Elasticsearch at the same time with a single atomic request.
     * NativeQuery: Allows us to write Elasticsearch queries originally in its own JSON format using Java DSL
     *  */
    public void markWinnerVotesInElastic(String winnerOptionId) {
        // Defines which documents should be affected by this update
        Query query = NativeQuery.builder()
                .withQuery(q -> q.bool(
                        b -> b.must(m -> m.term(t -> t.field("optionId").value(winnerOptionId)))
                                .mustNot(m -> m.term(t -> t.field("isWinner").value(true)))
                )).build();

        // Instead of fetching and saving back, we send a script to execute on the ES server.
        UpdateQuery updateQuery = UpdateQuery.builder(query)
                .withScript("ctx._source.isWinner = true")
                .withLang("painless")
                .build();

        // Execute the bulk update operation on the specified index.
        ByQueryResponse response = elasticsearchOperations.updateByQuery(updateQuery, IndexCoordinates.of("votes"));

        // Track how many documents were actually modified.
        long updatedVotes = response.getUpdated();
        sendToLog(updatedVotes);


     }
     public void sendToLog(long updatedVotes) {
         systemActionLogger.execute(
                 "VOTES",
                 "UPDATE_WINNER_VOTERS",
                 "Winner voter count: "+ updatedVotes,
                 "votes",
                 () -> {}
         );
     }
}
