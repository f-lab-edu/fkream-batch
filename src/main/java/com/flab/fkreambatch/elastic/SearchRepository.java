package com.flab.fkreambatch.elastic;

import com.flab.fkreambatch.entity.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchRepository extends ElasticsearchRepository<SearchDocument, Long> {

}
