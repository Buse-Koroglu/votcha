package com.example.votify_meet.users.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @UuidGenerator
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;


}
