package com.synoriq.synofin.collection.collectionservice.config.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synoriq.synofin.collection.collectionservice.common.GlobalVariables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class DBInitialization {
    private final Environment configUtility;

    public DBInitialization(Environment env) {
        this.configUtility = env;
    }

    private String getDbDriver() {
        return configUtility.getProperty("datasource.collection.driver-class-name");
    }

    private String getDbUrl() {
        return configUtility.getProperty("datasource.collection.jdbc-url");
    }

    private String getDbReadOnlyUrl() {
        return configUtility.getProperty("datasource.collection.read-only-jdbc-url");
    }

    private String getDbUserName() {
        return configUtility.getProperty("datasource.collection.username");
    }

    private String getDbPassword() {
        return configUtility.getProperty("datasource.collection.password");
    }

    private String getDbConnectionProperties() {
        return configUtility.getProperty("datasource.collection.connection-properties");
    }

    public Map<Object, Object> getDataSourceHashMap(String type) {
        List<String> clientMasterList = fetchClientList();

        Assert.notEmpty(clientMasterList, "Client master list must not be empty");

        //Setting the client master list globally
        GlobalVariables.clientMasterList = clientMasterList;

        HashMap<Object, Object> hashMap = new HashMap<>();

        if (!clientMasterList.isEmpty()) {
            for (String client : clientMasterList) {
                log.info("client List For DB Access = " + client);
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                Properties connectionProperty = new Properties();
                connectionProperty.setProperty("maxActive", "100");
                connectionProperty.setProperty("maxIdle", "10");
                log.info("Datbase driver = " + getDbDriver());
                if(client.equals("synoriq")) {
                    dataSource.setDriverClassName(getDbDriver());
                    dataSource.setUrl(getDbUrl() + client + (getDbConnectionProperties() != null ? getDbConnectionProperties() : ""));
                    dataSource.setUsername(getDbUserName());
                    dataSource.setPassword(getDbPassword());
                    dataSource.setConnectionProperties(connectionProperty);
                    log.info("client datasource url = " + dataSource.getUrl());
                    hashMap.put(client, dataSource);
                }
            }
        } else {
            throw new NullPointerException("Client Array not defined");
        }

        return hashMap;
    }

    public List<String> fetchClientList(){
        List<String> clientArray = new ArrayList<>();
        try {
            String response = new RestTemplate().getForObject(Objects.requireNonNull(configUtility.getProperty("clientList.api.url")), String.class);
            log.info("Response : {}", response);

            JsonArray array = new Gson()
                    .fromJson(response, JsonObject.class)
                    .getAsJsonArray("data");
            log.info("Json Array Data : {}", array);

            for (int i = 0; i < array.size(); i++) {
                clientArray.add(array.get(i).getAsJsonObject().getAsJsonPrimitive("clientId").getAsString());
            }
        } catch (Exception e){
            log.error("Exception occurred while fetching client list.", e);
        }
        return clientArray;
    }
}
