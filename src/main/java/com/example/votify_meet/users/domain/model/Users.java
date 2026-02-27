package com.example.votify_meet.users.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;


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

    // todo - Event Relation

}
