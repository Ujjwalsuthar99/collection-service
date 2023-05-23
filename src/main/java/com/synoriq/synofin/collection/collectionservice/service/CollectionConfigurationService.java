package com.synoriq.synofin.collection.collectionservice.service;

import java.util.Map;

public interface CollectionConfigurationService {

    public Map<String, String> getCollectionConfiguration(String token) throws Exception;

}
