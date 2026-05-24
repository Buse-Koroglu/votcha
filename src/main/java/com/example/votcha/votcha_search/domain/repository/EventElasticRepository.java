package com.example.votcha.votcha_search.domain.repository;

import com.example.votcha.votcha_search.domain.model.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface EventElasticRepository extends ElasticsearchRepository<EventDocument,String> {

    long count();

    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}")
    long countByCreatedAtAfter(Instant time);

}
