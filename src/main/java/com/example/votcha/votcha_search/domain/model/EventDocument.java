package com.example.votcha.votcha_search.domain.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Document(indexName = "events")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class EventDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant deadline;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String creatorName;

    @Field(type = FieldType.Keyword)
    private String creatorEmail;

    @Field(type = FieldType.Long)
    private Long totalVoteCount;

    @Field(type = FieldType.Object)
    private List<OptionDocument> options;

}
