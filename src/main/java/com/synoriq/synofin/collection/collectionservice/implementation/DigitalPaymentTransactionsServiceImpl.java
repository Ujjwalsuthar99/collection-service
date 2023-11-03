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

import java.util.ArrayList;
import java.util.Date;
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
    public Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws Exception {
        List<Map<String, Object>> digitalPaymentTransactionsEntityList;
        try {
            Pageable pageable = PageRequest.of(page, size);
            digitalPaymentTransactionsEntityList = digitalPaymentTransactionsRepository.getDigitalPaymentTransactionsByCreatedBy(userId, pageable, fromDate, toDate);

        } catch (Exception e) {
            throw new Exception("1017000");
        }

        return new BaseDTOResponse<Object>(digitalPaymentTransactionsEntityList);
    }

}
