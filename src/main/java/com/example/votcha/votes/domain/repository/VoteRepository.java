package com.example.votcha.votes.domain.repository;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.votes.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote,String> {
    boolean existsByVoterIdAndOption_Event_Id(String voterId, String eventId);
    Optional<Vote> findByVoter_IdAndOption_Event_Id(String voterId, String eventId);
    Optional<Vote> findByIdAndVoter(String id, Users user);
    Optional<List<Vote>> findAllByOption_Event_Id(String eventId);
    long countByOption_Event_Id(String eventId);
}
