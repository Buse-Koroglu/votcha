package com.example.votify_meet.events.domain.repository;

import com.example.votify_meet.events.domain.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepo extends JpaRepository<Events, String> {
}
