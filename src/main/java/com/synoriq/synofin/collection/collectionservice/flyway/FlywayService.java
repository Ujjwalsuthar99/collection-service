package com.synoriq.synofin.collection.collectionservice.flyway;

import com.synoriq.synofin.collection.collectionservice.config.db.CustomDBRouting;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FlywayService {

    @Autowired
    private FlywayProperties flywayProps;

    public FluentConfiguration getCommonFlywayConfig() {
        return Flyway.configure()
                .table(flywayProps.getTable())
                .locations(flywayProps.getCommonMigrationFilesLocation());
    }

    public FluentConfiguration getClientSpecificFlywayConfig(String clientName) {
        return Flyway.configure()
                .table(flywayProps.getTable().concat("_").concat(clientName))
                .locations(flywayProps.getClientSpecificMigrationFilesLocation().concat(clientName));
    }

    /**
     * Funtion to migrate schema for all the clients with client specific file
     * @throws Exception
     */
//    @PostConstruct
    public void migrateAllClient() throws Exception {
        for (Map.Entry<Object, DataSource> entry : (
                ((CustomDBRouting) flywayProps.getDataSource()).getResolvedDataSources().entrySet())
        ) {
            String clientId = (String) entry.getKey();
            log.info("Migrate database through flyway for client = " + clientId);
            executeFlyway(clientId, entry, getConfigurations(clientId, false));
        }
    }

    public List<FluentConfiguration> getConfigurations(String clientId, boolean isNewClient){
        List<FluentConfiguration> configurationList = new ArrayList<>();
        configurationList.add(getCommonFlywayConfig());
        if(!isNewClient)
            configurationList.add(getClientSpecificFlywayConfig(clientId));
        return configurationList;
    }

    public void executeFlyway(String clientId, Map.Entry<Object, DataSource> entry, List<FluentConfiguration> configurationList) throws Exception{
        DataSource dataSource = (DataSource) entry.getValue();
        Flyway flyway = null;
        try {
            log.info("Migrating Schema For Client Id : {}", clientId);
            for (FluentConfiguration config : configurationList) {
                log.info("config and dataSource + {} {}", config, dataSource);
                flyway = loadFlywayConfig(config, dataSource);
                flyway.migrate();
                closeConnection(dataSource, flyway);
            }
        } catch (Exception e){
            closeConnection(dataSource, flyway);
            log.error("Exception occurred while executing flyway for client id : {}", clientId, e);
            throw new Exception("Exception occurred for Client Id : "+clientId+ "\n"+ e);
        }
    }

    private Flyway loadFlywayConfig(FluentConfiguration config, DataSource dataSource) throws Exception {
        return config
                .defaultSchema(flywayProps.getDefaultSchema())
                .schemas(flywayProps.getSchemas())
                .baselineOnMigrate(flywayProps.isBaselineOnMigrate())
                .baselineVersion(flywayProps.getBaseLineVersion())
                .dataSource(dataSource)
                .outOfOrder(flywayProps.isOutOfOrder())
                .load();
    }

    public void closeConnection(DataSource dataSource, Flyway flyway) throws Exception{
        if(flyway != null) flyway.getConfiguration().getDataSource().getConnection().close();
        dataSource.getConnection().close();
    }

}
