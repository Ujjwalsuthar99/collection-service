package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.CollectionLimitUserWise.CollectionLimitUserWiseFetchDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ProfileService;
import com.synoriq.synofin.collection.collectionservice.service.CollectionLimitUserWiseService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
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
    private CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;
    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Autowired
    private UtilityService utilityService;
    @Autowired
    private ProfileService profileService;

    @Override
    public Object getCollectionLimitUserWise(String token, String userId) throws Exception {
        try {
            List<CollectionLimitUserWiseEntity> collectionLimitUserWiseEntityList = collectionLimitUserWiseRepository.getAllCollectionLimitUserWiseByUserId(Long.valueOf(userId));
            CollectionLimitUserWiseFetchDataResponseDTO collectionLimitUserWiseFetchDataResponseDTO =
                    collectionLimitUserWiseEntityList.stream().collect(
                            CollectionLimitUserWiseFetchDataResponseDTO::new,
                            (dto, entity) -> {
                                switch (entity.getCollectionLimitStrategiesKey()) {
                                    case "cash":
                                        dto.setCashLimit(entity.getTotalLimitValue());
                                        break;
                                    case "cheque":
                                        dto.setChequeLimit(entity.getTotalLimitValue());
                                        break;
                                    case "upi":
                                        dto.setUpiLimit(entity.getTotalLimitValue());
                                        break;
                                    case "rtgs":
                                        dto.setRtgsLimit(entity.getTotalLimitValue());
                                        break;
                                    default:
                                        dto.setNeftLimit(entity.getTotalLimitValue());
                                        break;
                                }
                            },
                            (dto1, dto2) -> {
                                dto1.setCashLimit(dto2.getCashLimit());
                                dto1.setChequeLimit(dto2.getChequeLimit());
                                dto1.setUpiLimit(dto2.getUpiLimit());
                                dto1.setNeftLimit(dto2.getNeftLimit());
                                dto1.setRtgsLimit(dto2.getRtgsLimit());
                            }
                    );
            return new BaseDTOResponse<>(collectionLimitUserWiseFetchDataResponseDTO);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

    }

    @Override
    public String createCollectionLimitUserWise(String token, CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) throws Exception {

        Long userId;
        String name;
        UserDetailByUserIdDTOResponse userDetailByUserIdDTOResponse;
        String userName = utilityService.splitCodeName(collectionLimitUserWiseDtoRequest.getUsername());
        ProfileDetailResponseDTO profileDetailResponseDTO;
        if (collectionLimitUserWiseDtoRequest.getUserId() != null) { // update
            userDetailByUserIdDTOResponse = utilityService.getUserDetailsByUserId(token, collectionLimitUserWiseDtoRequest.getUserId());
            userId = collectionLimitUserWiseDtoRequest.getUserId();
            name = userDetailByUserIdDTOResponse.getData().getEmployeeName();
            userName = userDetailByUserIdDTOResponse.getData().getEmployeeUserName();
            log.info(" if user name {}", userName);
        } else {
            profileDetailResponseDTO = profileService.getProfileDetails(token, userName);
            if (profileDetailResponseDTO.getData() != null) {
                userId = profileDetailResponseDTO.getData().getUserId();
                name = profileDetailResponseDTO.getData().getName();
//                userName = utilityService.splitCodeName(collectionLimitUserWiseDtoRequest.getUsername());
                log.info(" else user name {}", userName);
            } else {
                throw new Exception("1016041");
            }
        }

        CollectionLimitUserWiseEntity existingLimit = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(userId, collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey());

        if (existingLimit != null) {
            if (collectionLimitUserWiseDtoRequest.getTotalLimitValue() < existingLimit.getUtilizedLimitValue()) {
                throw new Exception("1017009");
            }
            existingLimit.setTotalLimitValue(collectionLimitUserWiseDtoRequest.getTotalLimitValue());
            existingLimit.setName(name);
            collectionLimitUserWiseRepository.save(existingLimit);
        } else {
            String defaultLimit = collectionLimitUserWiseDtoRequest.getTotalLimitValue().toString();
            if (collectionLimitUserWiseDtoRequest.getTotalLimitValue() == null) {
                if (collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey().equals("cash")) {
                    defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT);
                } else if (collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey().equals("cheque")) {
                    defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT);
                } else {
                    defaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT);
                }
            }
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
            collectionLimitUserWiseEntity.setDeleted(false);
            collectionLimitUserWiseEntity.setName(name);
            collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUserWiseDtoRequest.getCollectionLimitStrategiesKey());
            collectionLimitUserWiseEntity.setUserId(userId);
            collectionLimitUserWiseEntity.setUserName(userName);
            collectionLimitUserWiseEntity.setTotalLimitValue(Double.parseDouble(defaultLimit));
            collectionLimitUserWiseEntity.setUtilizedLimitValue(0D);
            collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
        }

        return "Data saved successfully";

    }
}
