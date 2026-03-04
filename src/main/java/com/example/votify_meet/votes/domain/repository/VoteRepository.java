package com.example.votify_meet.votes.domain.repository;
import com.example.votify_meet.votes.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,String> {
    boolean existsByVoterIdAndOption_EventId(String voterId, String eventId);
}
