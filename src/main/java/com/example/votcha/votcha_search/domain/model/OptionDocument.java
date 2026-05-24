package com.example.votcha.votcha_search.domain.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OptionDocument {
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Integer)
    private Integer voteCount;

    @Field(type = FieldType.Boolean)
    private boolean isWinner = false;
}
