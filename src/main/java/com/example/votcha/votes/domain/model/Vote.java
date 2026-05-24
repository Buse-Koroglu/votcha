package com.example.votcha.votes.domain.model;

import com.example.votcha.options.domain.model.Option;
import com.example.votcha.users.domain.model.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vote",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"option_id", "voter_id"})
    }
) // Users cannot vote more than once
public class Vote {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users voter;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @Formula("(SELECT o.event_id FROM option o WHERE o.id = option_id)")
    private String eventId;
}
