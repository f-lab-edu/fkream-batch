package com.flab.fkreambatch.dataSource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datasource.external")
@Setter
@Getter
public class ExternalDataSource {
    private String driverClassName;
    private String jdbcUrl;
    private String username;
    private String password;
}
