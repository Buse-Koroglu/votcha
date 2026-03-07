package com.example.votify_meet.votes.domain.repository;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote,String> {
    boolean existsByVoterIdAndOption_Event_Id(String voterId, String eventId);
    Optional<Vote> findByIdAndVoter(String id, Users user);
}
