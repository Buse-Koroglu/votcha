package com.example.votcha.auth.domain.model;

import com.example.votcha.users.domain.model.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name= "refresh_token")
public class RefreshToken {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false,unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private boolean isRevoked;

    private String ipAddress;

}
