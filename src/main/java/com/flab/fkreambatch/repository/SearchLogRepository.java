package com.flab.fkreambatch.repository;

import com.flab.fkreambatch.entity.SearchLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLogRepository extends MongoRepository<SearchLog, String> {


}
