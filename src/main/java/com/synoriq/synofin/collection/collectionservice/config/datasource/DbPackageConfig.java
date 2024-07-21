package com.synoriq.synofin.collection.collectionservice.config.datasource;

import com.synoriq.db_connection_management.annotations.LoadAdditionalDatabase;
import com.synoriq.db_connection_management.constant.service.DbEnum;
import com.synoriq.db_connection_management.interfaces.DbConnectionManagement;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@LoadAdditionalDatabase(
        dataSources = {
                DbEnum.clients
        }
)
@ComponentScan("com.synoriq.db_connection_management")
public class DbPackageConfig implements DbConnectionManagement {
}