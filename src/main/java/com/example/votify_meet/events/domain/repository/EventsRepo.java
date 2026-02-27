package com.example.votify_meet.events.domain.repository;

import com.example.votify_meet.events.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepo extends JpaRepository<Event, String> {
}
