package com.example.votcha.votcha_search.domain.repository;

import com.example.votcha.votcha_search.domain.model.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

@Repository
public interface EventElasticRepository extends ElasticsearchRepository<EventDocument,String> {
    List<EventDocument> findByCreatorName(String creatorName);

    List<EventDocument> findByCreatorEmail(String creatorEmail);

    long count();

    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}")
    long countByCreatedAtAfter(Instant time);

    default List<EventDocument> findAllByOrderByTotalVoteCountDesc(Pageable pageable) // we can take most voted n event (n is optional by pageable)
    {
        return null;
    }
}
