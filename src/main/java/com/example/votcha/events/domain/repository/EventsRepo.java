package com.example.votcha.events.domain.repository;

import com.example.votcha.events.domain.model.Event;
import com.example.votcha.users.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepo extends JpaRepository<Event, String> {
    @Query("SELECT e FROM Event e WHERE e.creator.id=:userId")
    Optional<List<Event>> findAllByCreatorId(@Param(value = "userId") String userId);

    Optional<Event> findByIdAndCreator(String eventId, Users user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Event e\s
            SET e.type = 'STANDARD'
            WHERE e.type = 'SURPRISED'
            AND e.deadline < :deadline
           \s""")
    int setStandardExpiredSurprisedEvents(Instant deadline);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Event e\s
            SET e.status = 'CLOSED'
            WHERE e.status = 'OPEN'
            AND e.deadline < :deadline
           \s""")
    int closeExpiredEvents(Instant deadline);

}
