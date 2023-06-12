package com.flab.fkreambatch.config;

import com.flab.fkreambatch.dataSource.ExternalDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FkreamDataSourceConfig {

    @Autowired
    ExternalDataSource externalDataSource;

    @Bean
    public DataSource fkreamDataSource() {
        return DataSourceBuilder.create()
            .username(externalDataSource.getUsername())
            .password(externalDataSource.getPassword())
            .url(externalDataSource.getJdbcUrl())
            .driverClassName(externalDataSource.getDriverClassName())
            .build();
    }
}
