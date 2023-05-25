package com.synoriq.synofin.collection.collectionservice.implementation;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.CollectionLimitUserWise.CollectionLimitUserWiseFetchDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ProfileService;
import com.synoriq.synofin.collection.collectionservice.service.CollectionLimitUserWiseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class CollectionLimitUserWiseServiceImpl implements CollectionLimitUserWiseService {

    @Autowired
    CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;
    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Autowired
    ProfileService profileService;

    @Override
    public Object getCollectionLimitUserWise(String token, String userId) throws Exception {
        try {
            List<CollectionLimitUserWiseEntity> collectionLimitUserWiseEntityList = collectionLimitUserWiseRepository.getAllCollectionLimitUserWiseByUserId(Long.valueOf(userId));

            CollectionLimitUserWiseFetchDataResponseDTO collectionLimitUserWiseFetchDataResponseDTO = new CollectionLimitUserWiseFetchDataResponseDTO();
            for (CollectionLimitUserWiseEntity collectionLimitUserWiseEntity : collectionLimitUserWiseEntityList) {
                if (collectionLimitUserWiseEntity.getCollectionLimitStrategiesKey().equals("cash")) {
                    collectionLimitUserWiseFetchDataResponseDTO.setCashLimit(collectionLimitUserWiseEntity.getTotalLimitValue());
                } else if (collectionLimitUserWiseEntity.getCollectionLimitStrategiesKey().equals("cheque")) {
                    collectionLimitUserWiseFetchDataResponseDTO.setChequeLimit(collectionLimitUserWiseEntity.getTotalLimitValue());
                } else {
                    collectionLimitUserWiseFetchDataResponseDTO.setUpiLimit(collectionLimitUserWiseEntity.getTotalLimitValue());
                }

            }


            return new BaseDTOResponse<>(collectionLimitUserWiseFetchDataResponseDTO);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

    }

    @Override
    public String createCollectionLimitUserWise(String token, CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) throws Exception {


        ProfileDetailResponseDTO profileDetailResponseDTO = profileService.getProfileDetails(token, collectionLimitUserWiseDtoRequest.getUsername());

        CollectionLimitUserWiseEntity existingLimit = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(profileDetailResponseDTO.getData().getUserId(), collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey());

        String defaultLimit = null;
        if(collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey().equals("cash")) {
            defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT);
        } else if(collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey().equals("cheque")) {
            defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT);
        } else {
            defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT);
        }

        if (existingLimit != null) {
            if (collectionLimitUserWiseDtoRequest.getTotalLimitValue() < existingLimit.getUtilizedLimitValue()) {
                throw new Exception("1017009");
            }
            existingLimit.setTotalLimitValue(collectionLimitUserWiseDtoRequest.getTotalLimitValue());
            collectionLimitUserWiseRepository.save(existingLimit);
        } else {
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
            collectionLimitUserWiseEntity.setCreatedDate(new Date());
            collectionLimitUserWiseEntity.setDeleted(false);
            collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey());
            collectionLimitUserWiseEntity.setUserId(profileDetailResponseDTO.getData().getUserId());
            collectionLimitUserWiseEntity.setUserName(profileDetailResponseDTO.getData().getUserName());
            collectionLimitUserWiseEntity.setTotalLimitValue(Double.valueOf(defaultLimit));
            collectionLimitUserWiseEntity.setUtilizedLimitValue(0D);
            collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
        }

        return "Data saved successfully";

    }
}
