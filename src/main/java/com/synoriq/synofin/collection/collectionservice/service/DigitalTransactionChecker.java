package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;

public interface DigitalTransactionChecker {

    Object digitalTransactionStatusCheck(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws CustomException, InterruptedException;

}
