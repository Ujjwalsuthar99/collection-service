package com.synoriq.synofin.lms.reportsservice.flyway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Data
@Configuration
@ConfigurationProperties(prefix = "spring.flyway")
@Slf4j
public class FlywayProperties {

    private String[] schemas = new String[0];
    private String defaultSchema;
    private String commonMigrationFilesLocation;
    private String clientSpecificMigrationFilesLocation;
    private String table;
    private String baseLineVersion;
    private boolean baselineOnMigrate;
    private boolean outOfOrder;

    @Autowired
    private DataSource dataSource;

}
