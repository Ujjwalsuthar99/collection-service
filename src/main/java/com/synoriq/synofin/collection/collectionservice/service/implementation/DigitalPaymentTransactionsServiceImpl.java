package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.factory.TransactionStatusCheckerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DigitalPaymentTransactionsServiceImpl implements DigitalPaymentTransactionsService {

    public DigitalPaymentTransactionsServiceImpl(DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository, TransactionStatusCheckerFactory transactionStatusCheckerFactory) {
        this.digitalPaymentTransactionsRepository = digitalPaymentTransactionsRepository;
        this.transactionStatusCheckerFactory = transactionStatusCheckerFactory;
    }
    private final DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    private final TransactionStatusCheckerFactory transactionStatusCheckerFactory;

    @Override
    public Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws Exception {
        toDate = DateUtils.addDays(toDate,1);
        List<Map<String, Object>> digitalPaymentTransactionsEntityList;
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            digitalPaymentTransactionsEntityList = digitalPaymentTransactionsRepository.getDigitalPaymentTransactionsByCreatedBy(userId, pageable, fromDate, toDate);
            if (!digitalPaymentTransactionsEntityList.isEmpty()) {
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


    @Override
    public Object checkDigitalPaymentStatus(String token, CommonTransactionStatusCheckRequestDTO body) throws Exception {
        DigitalPaymentTransactionsEntity digitalPaymentTransactions = digitalPaymentTransactionsRepository.findByMerchantTranId(body.getMerchantTranId());
        DigitalTransactionChecker checker = transactionStatusCheckerFactory.getChecker(digitalPaymentTransactions.getPaymentServiceName());
        return checker.digitalTransactionStatusCheck(token, body);
    }


}
