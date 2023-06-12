package com.flab.fkreambatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableBatchProcessing
public class FkreamBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FkreamBatchApplication.class, args);
	}

}
