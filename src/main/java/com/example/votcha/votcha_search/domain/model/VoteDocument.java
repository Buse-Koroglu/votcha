package com.example.votcha.votcha_search.domain.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "votes")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VoteDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String optionId;

    @Field(type = FieldType.Text)
    private String optionContent;

    @Field(type = FieldType.Keyword)
    private String voterId;

    @Field(type = FieldType.Text)
    private String voterFullName;

    @Field(type = FieldType.Keyword)
    private String eventId;

    @Field(type = FieldType.Text)
    private String eventTitle;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;

    @Field(type = FieldType.Boolean)
    private boolean isWinner;

}
