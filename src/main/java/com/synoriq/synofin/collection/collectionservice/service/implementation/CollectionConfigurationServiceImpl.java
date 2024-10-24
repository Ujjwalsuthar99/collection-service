package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.service.CollectionConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CollectionConfigurationServiceImpl implements CollectionConfigurationService {

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Override
    public Map<String, String> getCollectionConfiguration(String token) throws CollectionException {
        List<CollectionConfigurationsEntity> collectionConfigurationsEntityList = collectionConfigurationsRepository.findAll();
        try {
            Map<String, String> objectData = new HashMap<>();
            for (CollectionConfigurationsEntity collectionConfigurationsEntity : collectionConfigurationsEntityList) {
                objectData.put(collectionConfigurationsEntity.getConfigurationName(), collectionConfigurationsEntity.getConfigurationValue());
            }
            return objectData;
        } catch(Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(1017002);
            throw new CollectionException(errCode, 1017002);
        }

    }
}
