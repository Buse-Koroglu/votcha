package com.example.votcha.votcha_search.domain.repository;

import com.example.votcha.users.domain.model.Role;
import com.example.votcha.votcha_search.domain.model.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserElasticRepository extends ElasticsearchRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);

    List<UserDocument> findByFullName(String fullName);

    List<UserDocument> findByRole(Role role);

    /**
     * @return total count of registered users
     */
    long count();

    /**
    ** @return Counts how many people have been registered today
    */
    @Query("{\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}")
    long countByCreatedAtAfter(Instant time);
}
