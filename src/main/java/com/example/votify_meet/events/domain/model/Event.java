package com.example.votify_meet.events.domain.model;

import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.users.domain.model.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;
;

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

    public void addOptions(List<Option> newOptions){
        this.options = newOptions;
        newOptions.forEach(option -> option.setEvent(this));

    }
    public boolean isExpired(){return Instant.now().isAfter(deadline);}

}
