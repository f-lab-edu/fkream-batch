package com.flab.fkreambatch.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "ranking")
public class SearchDocument {

    @Field(type = FieldType.Keyword)
    private String searchWord;

    @Field(type = FieldType.Integer)
    private Integer searchCount;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
}
