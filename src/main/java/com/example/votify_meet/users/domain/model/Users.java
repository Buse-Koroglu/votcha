package com.example.votify_meet.users.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class Users implements UserDetails {
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

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    public void prePersist() {
        if(this.role == null) {
            this.role = Role.USER;
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getUsername(){
        return email;
    }
    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public boolean isEnabled() {return true;}
}
