package com.flab.fkreambatch.repository;

import com.flab.fkreambatch.entity.DealStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealStatisticsRepository extends JpaRepository<DealStatisticsEntity, Long> {

}
