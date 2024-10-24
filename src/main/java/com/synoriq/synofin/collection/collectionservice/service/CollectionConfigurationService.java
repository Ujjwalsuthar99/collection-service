package com.synoriq.synofin.collection.collectionservice.service;

import java.util.Map;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;

public interface CollectionConfigurationService {

    public Map<String, String> getCollectionConfiguration(String token) throws CollectionException;

}
