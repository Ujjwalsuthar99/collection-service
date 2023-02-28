package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CollectionConfigurationService {

    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

    public Map<String, String> getCollectionConfiguration() throws Exception {
        List<CollectionConfigurationsEntity> collectionConfigurationsEntityList = collectionConfigurationsRepository.findAll();
        try {
            Map<String, String> objectData = new HashMap<>();
            for (CollectionConfigurationsEntity collectionConfigurationsEntity : collectionConfigurationsEntityList) {
                objectData.put(collectionConfigurationsEntity.getConfigurationName(), collectionConfigurationsEntity.getConfigurationValue());
            }
            return objectData;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }
}
