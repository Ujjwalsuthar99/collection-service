package com.synoriq.synofin.collection.collectionservice.config.dbrouting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Slf4j
public class DBInitialization {

    private final Environment configUtility;

    public DBInitialization(Environment env) {
        this.configUtility = env;
    }

    private String getDbDriver() {
        return configUtility.getProperty("datasource.scf.driver-class-name");
    }

    private String getDbUrl() {
        return configUtility.getProperty("datasource.scf.jdbc-url");
    }

    private String getDbUserName() {
        return configUtility.getProperty("datasource.scf.username");
    }

    private String getDbPassword() {
        return configUtility.getProperty("datasource.scf.password");
    }


    public Map<Object, Object> getDataSourceHashMap() {
        List<String> clientMasterList = fetchClientList();

        Assert.notEmpty(clientMasterList, "Client master list must not be empty");
        HashMap<Object, Object> hashMap = new HashMap<>();

        if (!clientMasterList.isEmpty()) {
            for (String client : clientMasterList) {
                log.info("client List For DB Access = " + client);
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                Properties connectionProperty = new Properties();
                connectionProperty.setProperty("maxActive", "100");
                connectionProperty.setProperty("maxIdle", "10");
                dataSource.setDriverClassName(getDbDriver());
                dataSource.setUrl(getDbUrl() + client);
                dataSource.setUsername(getDbUserName());
                dataSource.setPassword(getDbPassword());
                dataSource.setConnectionProperties(connectionProperty);
                hashMap.put(client, dataSource);
            }
        } else {
            throw new NullPointerException("Client Array not defined");
        }

        return hashMap;
    }

    public List<String> fetchClientList(){
        List<String> clientArray = new ArrayList<>();
        clientArray.add("choice");
        clientArray.add("finture");
        clientArray.add("test");
//        try {
//            String response = new RestTemplate().getForObject(Objects.requireNonNull(configUtility.getProperty("clientList.api.url")), String.class);
//            log.info("Response : {}", response);
//
//            JsonArray array = new Gson()
//                    .fromJson(response, JsonObject.class)
//                    .getAsJsonArray("data");
//            log.info("Json Array Data : {}", array);
//
//            for (int i = 0; i < array.size(); i++) {
//                clientArray.add(array.get(i).getAsJsonObject().getAsJsonPrimitive("clientId").getAsString());
//            }
//        } catch (Exception e){
//            log.error("Exception occurred while fetching client list.", e);
//        }
        return clientArray;
    }

}