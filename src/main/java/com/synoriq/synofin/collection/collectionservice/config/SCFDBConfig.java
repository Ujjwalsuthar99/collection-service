package com.synoriq.synofin.collection.collectionservice.config;

import com.synoriq.synofin.odapplicationcreationservice.config.dbrouting.CustomRoutingDataSource;
import com.synoriq.synofin.odapplicationcreationservice.config.dbrouting.DBInitialization;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.synoriq.synofin.odcommonpersistence.*"}
)
@EntityScan("com.synoriq.synofin.odcommonpersistence.entity*")
public class SCFDBConfig {
    @Resource
    public Environment env;

    @Bean("scfDataSource")
    public DataSource dataSource() {
        CustomRoutingDataSource lmsDataSource = new CustomRoutingDataSource();
        lmsDataSource.setTargetDataSources(new DBInitialization(env).getDataSourceHashMap());
        return lmsDataSource;
    }

}
