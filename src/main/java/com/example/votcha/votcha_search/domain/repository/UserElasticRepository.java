package com.example.votcha.votcha_search.domain.repository;

import com.example.votcha.votcha_search.domain.model.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserElasticRepository extends ElasticsearchRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);

    List<UserDocument> findByFullName(String fullName);

    List<UserDocument> findByRole(String role);

}
