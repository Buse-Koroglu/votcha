package com.example.votify_meet.events.domain.repository;

import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepo extends JpaRepository<Event, String> {
    @Query("SELECT e FROM Event e WHERE e.creator.id=:userId")
    Optional<List<Event>> findAllByCreatorId(@Param(value = "userId") String userId);

    Optional<Event> findByIdAndCreator(String eventId, Users user);


}
