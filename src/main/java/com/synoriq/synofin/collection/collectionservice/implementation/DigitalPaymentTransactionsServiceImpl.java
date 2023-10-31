package com.synoriq.synofin.collection.collectionservice.implementation;


import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.DigitalPaymentTransactionsService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DigitalPaymentTransactionsServiceImpl implements DigitalPaymentTransactionsService {
    @Autowired
    private DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;

    @Autowired
    private UtilityService utilityService;
    @Override
    public Object getDigitalPaymentTransactionsUserWise(String token, String userId, Integer page, Integer size) throws Exception {
        BaseDTOResponse<Object> baseDTOResponse;
        Pageable pageable = PageRequest.of(page,size);

        List<Map<String, Object>> digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.getDigitalPaymentTransactionsByCreatedBy(Long.valueOf(userId), pageable);

        if (digitalPaymentTransactionsEntity != null) {
            baseDTOResponse = new BaseDTOResponse<>(digitalPaymentTransactionsEntity);
        } else {
            log.error("Digital payment transactions data not found for user this id {}", userId);
            throw new Exception("1016025");
        }

        return baseDTOResponse;



    }
}
