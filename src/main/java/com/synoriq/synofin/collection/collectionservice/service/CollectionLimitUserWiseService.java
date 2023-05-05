package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.collectionLimitUserWise.CollectionLimitUserWiseFetchDataResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class CollectionLimitUserWiseService {

    @Autowired
    CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;

    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

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


    public String createCollectionLimitUserWise(String token, String userId, CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) throws Exception {
            log.info("cash limit {}", collectionLimitUserWiseDtoRequest.getCash());
            log.info("cheque limit {}", collectionLimitUserWiseDtoRequest.getCheque());
            log.info("upi limit {}", collectionLimitUserWiseDtoRequest.getUpi());

            CollectionLimitUserWiseEntity existingCashLimit = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(Long.valueOf(userId), "cash");
            CollectionLimitUserWiseEntity existingChequeLimit = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(Long.valueOf(userId), "cheque");
            CollectionLimitUserWiseEntity existingUpiLimit = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(Long.valueOf(userId), "upi");

            String cashDefaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT);
            String chequeDefaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT);
            String digitalDefaultLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT);

            if (existingCashLimit != null) {
                if(collectionLimitUserWiseDtoRequest.getCash() < existingCashLimit.getUtilizedLimitValue()) {
                    throw new Exception("1017006");
                }
                existingCashLimit.setTotalLimitValue(collectionLimitUserWiseDtoRequest.getCash());
                collectionLimitUserWiseRepository.save(existingCashLimit);
            } else {
                CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                collectionLimitUserWiseEntity.setDeleted(false);
                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey("cash");
                collectionLimitUserWiseEntity.setUserId(Long.valueOf(userId));
                collectionLimitUserWiseEntity.setTotalLimitValue(Double.valueOf(cashDefaultLimit));
                collectionLimitUserWiseEntity.setUtilizedLimitValue(0D);
                collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
            }

            if (existingChequeLimit != null) {
                if(collectionLimitUserWiseDtoRequest.getCheque() < existingChequeLimit.getUtilizedLimitValue()) {
                    throw new Exception("1017007");
                }
                existingChequeLimit.setTotalLimitValue(collectionLimitUserWiseDtoRequest.getCheque());
                collectionLimitUserWiseRepository.save(existingChequeLimit);
            } else {
                CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                collectionLimitUserWiseEntity.setDeleted(false);
                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey("cheque");
                collectionLimitUserWiseEntity.setUserId(Long.valueOf(userId));
                collectionLimitUserWiseEntity.setTotalLimitValue(Double.valueOf(chequeDefaultLimit));
                collectionLimitUserWiseEntity.setUtilizedLimitValue(0D);
                collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
            }

            if (existingUpiLimit != null) {
                if(collectionLimitUserWiseDtoRequest.getUpi() < existingUpiLimit.getUtilizedLimitValue()) {
                    throw new Exception("1017008");
                }
                existingUpiLimit.setTotalLimitValue(collectionLimitUserWiseDtoRequest.getUpi());
                collectionLimitUserWiseRepository.save(existingUpiLimit);
            } else {
                CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                collectionLimitUserWiseEntity.setDeleted(false);
                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey("upi");
                collectionLimitUserWiseEntity.setUserId(Long.valueOf(userId));
                collectionLimitUserWiseEntity.setTotalLimitValue(Double.valueOf(digitalDefaultLimit));
                collectionLimitUserWiseEntity.setUtilizedLimitValue(0D);
                collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
            }


            return "Data saved successfully";

    }
}
