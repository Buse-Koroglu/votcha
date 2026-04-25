package com.example.votcha.events.domain.model;

import com.example.votcha.options.domain.model.Option;
import com.example.votcha.users.domain.model.Users;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {
    @Id
    @UuidGenerator
    private String id;

    private String title;
    private String description;
    private Instant deadline;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    private Instant updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users creator;

    /**Calculates the total cote count of an event using join.(Performance)*/
    @Formula("(SELECT COUNT(v.id) FROM vote v JOIN option o ON v.option_id = o.id WHERE o.event_id = id)")
    private Long totalVoteCount;


    public Long getTotalVoteCount() {
        return totalVoteCount == null ? 0L : totalVoteCount;
    }

    public void addOptions(List<Option> newOptions){
        this.options = newOptions;
        newOptions.forEach(option -> option.setEvent(this));

    }
    public boolean isExpired(){
        if(deadline == null){
            return false;
        }
        return Instant.now().isAfter(deadline);
    }

}
