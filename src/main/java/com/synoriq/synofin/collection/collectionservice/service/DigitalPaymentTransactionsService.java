package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;

import java.util.Date;

public interface DigitalPaymentTransactionsService {

    Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws CollectionException;

    Object checkDigitalPaymentStatus(String token, CommonTransactionStatusCheckRequestDTO object) throws CustomException, InterruptedException;

    DigitalPaymentTransactionsEntity createDigitalPaymentTransaction(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity) throws CustomException;

}
