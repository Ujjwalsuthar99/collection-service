package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CollectionConfigurationService {

    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

    public List<CollectionConfigurationDtoRequest> getCollectionConfiguration() throws Exception {

        List<CollectionConfigurationDtoRequest> collectionConfigurationDtoRequestList = new ArrayList<>();
        List<CollectionConfigurationsEntity> collectionConfigurationsEntityList = collectionConfigurationsRepository.findAll();
        try {
            for (CollectionConfigurationsEntity collectionConfigurationsEntity : collectionConfigurationsEntityList) {
                log.info("additionalContactDetailsEntity Data {}", collectionConfigurationsEntity);

                CollectionConfigurationDtoRequest collectionConfigurationDtoRequest = new CollectionConfigurationDtoRequest();
                collectionConfigurationDtoRequest.setCreatedDate(collectionConfigurationsEntity.getCreatedDate());
                collectionConfigurationDtoRequest.setCreatedBy(collectionConfigurationsEntity.getCreatedBy());
                collectionConfigurationDtoRequest.setConfigurationName(collectionConfigurationsEntity.getConfigurationName());
                collectionConfigurationDtoRequest.setConfigurationValue(collectionConfigurationsEntity.getConfigurationValue());
                collectionConfigurationDtoRequest.setConfigurationDescription(collectionConfigurationsEntity.getConfigurationDescription());
                collectionConfigurationDtoRequestList.add(collectionConfigurationDtoRequest);
            }
            return collectionConfigurationDtoRequestList;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }
}
