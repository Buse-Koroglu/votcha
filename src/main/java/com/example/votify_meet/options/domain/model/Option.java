package com.example.votify_meet.options.domain.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "options")
public class Option {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String content;
}
