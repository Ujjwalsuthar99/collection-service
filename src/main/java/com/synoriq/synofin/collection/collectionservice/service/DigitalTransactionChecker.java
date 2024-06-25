package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.CommonTransactionStatusCheckRequestDTO;

public interface DigitalTransactionChecker {

    Object digitalTransactionStatusCheck(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws Exception;

}
