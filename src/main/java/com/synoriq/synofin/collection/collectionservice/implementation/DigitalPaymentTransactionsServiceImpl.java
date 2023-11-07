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

import java.util.*;

@Service
@Slf4j
public class DigitalPaymentTransactionsServiceImpl implements DigitalPaymentTransactionsService {
    @Autowired
    private DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;

    @Autowired
    private UtilityService utilityService;
    @Override
    public Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws Exception {
        List<Map<String, Object>> digitalPaymentTransactionsEntityList = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            digitalPaymentTransactionsEntityList = digitalPaymentTransactionsRepository.getDigitalPaymentTransactionsByCreatedBy(userId, pageable, fromDate, toDate);
            if (digitalPaymentTransactionsEntityList.size() > 0) {
                response.put("transactions", digitalPaymentTransactionsEntityList);
                response.put("total_rows", digitalPaymentTransactionsEntityList.get(0).get("total_rows"));
            } else {
                response.put("transactions", digitalPaymentTransactionsEntityList);
                response.put("total_rows", 0);
            }

        } catch (Exception e) {
            throw new Exception("1017000");
        }

        return new BaseDTOResponse<Object>(response);
    }

}
