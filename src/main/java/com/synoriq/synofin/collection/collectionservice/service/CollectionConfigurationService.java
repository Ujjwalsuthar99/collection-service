package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CollectionConfigurationService {

    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Autowired
    UtilityService utilityService;

    public Map<String, String> getCollectionConfiguration(String token) throws Exception {
        List<CollectionConfigurationsEntity> collectionConfigurationsEntityList = collectionConfigurationsRepository.findAll();
        try {
            Map<String, String> objectData = new HashMap<>();
//            String logoFileName = null;
//            for (CollectionConfigurationsEntity collectionConfigurationsEntity : collectionConfigurationsEntityList) {
//                if (Objects.equals(collectionConfigurationsEntity.getConfigurationName(), "client_app_logo")) {
//                    logoFileName = collectionConfigurationsEntity.getConfigurationValue();
//                } else {
//                    objectData.put(collectionConfigurationsEntity.getConfigurationName(), collectionConfigurationsEntity.getConfigurationValue());
//                }
//            }
//            if (logoFileName != null) {
//                String clientId = logoFileName.split("/")[0];
//                DownloadBase64FromS3 res = utilityService.downloadBase64FromS3(token, "", logoFileName, clientId);
//                objectData.put("client_app_logo", res.getData());
//            }
            for (CollectionConfigurationsEntity collectionConfigurationsEntity : collectionConfigurationsEntityList) {
                objectData.put(collectionConfigurationsEntity.getConfigurationName(), collectionConfigurationsEntity.getConfigurationValue());
            }
            return objectData;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }
}
