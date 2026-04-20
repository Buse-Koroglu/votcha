package com.example.votcha.votcha_search.domain.model;

import com.example.votcha.users.domain.model.Role;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;


@Document(indexName = "users")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @Id
    private String id;

    // Makes searchable
    @Field(type = FieldType.Text, analyzer = "standard")
    private String fullName;

    // Exact match field (no tokenization)
    @Field(type =  FieldType.Keyword)
    private String email;

    // Enum stored as keyword for filtering
    @Field(type = FieldType.Keyword)
    private Role role;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;

}
