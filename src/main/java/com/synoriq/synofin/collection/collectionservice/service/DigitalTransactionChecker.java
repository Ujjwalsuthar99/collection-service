package com.synoriq.synofin.collection.collectionservice.service;

public interface DigitalTransactionChecker {

    Object digitalTransactionStatusCheck(String token, Object requestBody) throws Exception;

}
