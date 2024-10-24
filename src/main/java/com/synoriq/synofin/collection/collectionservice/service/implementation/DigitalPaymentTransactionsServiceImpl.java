package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.factory.TransactionStatusCheckerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.PAYMENT_LINK;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.PENDING;

@Service
@Slf4j
public class DigitalPaymentTransactionsServiceImpl implements DigitalPaymentTransactionsService {

    private static final String TOTAL_ROW = "total_rows";

    public DigitalPaymentTransactionsServiceImpl(DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository, TransactionStatusCheckerFactory transactionStatusCheckerFactory) {
        this.digitalPaymentTransactionsRepository = digitalPaymentTransactionsRepository;
        this.transactionStatusCheckerFactory = transactionStatusCheckerFactory;
    }
    private final DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    private final TransactionStatusCheckerFactory transactionStatusCheckerFactory;

    @Override
    public Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws CollectionException {
        toDate = DateUtils.addDays(toDate,1);
        List<Map<String, Object>> digitalPaymentTransactionsEntityList;
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            digitalPaymentTransactionsEntityList = digitalPaymentTransactionsRepository.getDigitalPaymentTransactionsByCreatedBy(userId, pageable, fromDate, toDate);
            if (!digitalPaymentTransactionsEntityList.isEmpty()) {
                response.put("transactions", digitalPaymentTransactionsEntityList);
                response.put(TOTAL_ROW, digitalPaymentTransactionsEntityList.get(0).get(TOTAL_ROW));
            } else {
                response.put("transactions", digitalPaymentTransactionsEntityList);
                response.put(TOTAL_ROW, 0);
            }

        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(1017000);
            throw new CollectionException(errCode, 1017000);
        }

        return new BaseDTOResponse<Object>(response);
    }


    @Override
    public Object checkDigitalPaymentStatus(String token, CommonTransactionStatusCheckRequestDTO body) throws CustomException, InterruptedException {
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactions = digitalPaymentTransactionsRepository.findByMerchantTranId(body.getMerchantTranId());
            DigitalTransactionChecker checker = transactionStatusCheckerFactory.getChecker(digitalPaymentTransactions.getPaymentServiceName());
            log.info("checker -> {}", checker);
            return checker.digitalTransactionStatusCheck(token, body);
        } catch(InterruptedException ie){
            log.error("Interrupted Exception Error {}", ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        } catch(Exception ee){
            throw new CustomException(ee.getMessage());
        }
    }

    @Override
    public DigitalPaymentTransactionsEntity createDigitalPaymentTransaction(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity) throws CustomException {
        try {
            digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
            return digitalPaymentTransactionsEntity;
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }
    }


}
