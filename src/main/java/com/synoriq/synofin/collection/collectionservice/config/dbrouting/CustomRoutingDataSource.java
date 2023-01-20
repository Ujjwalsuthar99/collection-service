package com.synoriq.synofin.collection.collectionservice.config.dbrouting;

import com.synoriq.synofin.odapplicationcreationservice.service.CurrentUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


@Slf4j
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    @Autowired
    CurrentUserInfo currentUserInfo;

    @Override
    protected Object determineCurrentLookupKey() {
        log.info("currentClientId27: {}", currentUserInfo.getClientId());
        return currentUserInfo.getClientId() != null ? currentUserInfo.getClientId() : "null";
    }

}