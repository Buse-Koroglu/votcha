package com.example.votcha.options.domain.model;
import com.example.votcha.events.domain.model.Event;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "option")
public class Option {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    /*
    * This field is not a column in the database. When fetching the Hibernate Option,
    * it executes this SQL in the background and directly writes the result here! (It is very performant)
    * */
    @Formula("(SELECT COUNT(v.id) FROM vote v WHERE v.option_id = id)")
    private Integer voteCount;

    public Integer getVoteCount() {return voteCount == null ? 0 : voteCount;}
    public String getUserId() {
        return event.getCreator().getId();
    }
    public String getEventId() {return event.getId();}

}
