package com.example.votify_meet.events.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Events {
    @Id
    @UuidGenerator
    private String id;

    private String title;
    private String description;
    private Instant createdAt;
    private Instant deadline;
    private Status status;
    private EventType type;

    // todo - User Relation
    // todo - Option Relation


}
