package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos.*;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;


public interface EmitraService {

    Object verifySsoToken(String token, VerifySsoTokenDTO requestBody) throws CustomException;
    Object serviceTransaction(String token, Long loanId, Long userId, ServiceTransactionDTO requestBody) throws CustomException;
    Object updateTransactionPosting(String token, Long loanId, UpdateTransactionPostingDTO requestBody) throws CustomException;
    Object verifyTransaction(String token, Long loanId, VerifyTransactionDTO requestBody) throws CustomException;
    Object getKioskDetails(String token, Long loanId, GetKioskDetailsDTO requestBody) throws CustomException;
    Object decryption(String token, DecryptionDTO requestBody) throws CustomException;

}
