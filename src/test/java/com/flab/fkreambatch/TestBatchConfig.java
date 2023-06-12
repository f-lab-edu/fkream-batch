package com.flab.fkreambatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@EntityScan("com.flab.fkreambatch.entity")
@EnableJpaRepositories("com.flab.fkreambatch.repository")
@EnableTransactionManagement
@EnableConfigurationProperties
public class TestBatchConfig {

}
