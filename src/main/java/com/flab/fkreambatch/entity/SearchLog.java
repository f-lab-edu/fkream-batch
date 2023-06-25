package com.flab.fkreambatch.entity;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.hibernate.annotations.Index;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SearchLog")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchLog {

    @Id
    private ObjectId id;
    private String content;
    private LocalDateTime createdAt;

    public SearchLog(String content, LocalDateTime createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }
}
