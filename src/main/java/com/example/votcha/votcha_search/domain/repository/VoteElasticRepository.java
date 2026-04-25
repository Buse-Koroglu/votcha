package com.example.votcha.votcha_search.domain.repository;

import com.example.votcha.votcha_search.domain.model.VoteDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface VoteElasticRepository extends ElasticsearchRepository<VoteDocument,String> {

    long count();

    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}")
    long countByCreatedAtAfter(Instant time);

    long countByVoterId(String voterId);

    long countByVoterIdAndIsWinnerTrue(String voterId);
}
