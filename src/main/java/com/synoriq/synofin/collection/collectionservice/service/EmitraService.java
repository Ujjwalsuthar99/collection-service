package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.emitraRequestDTOs.*;
import org.springframework.stereotype.Service;

@Service
public interface EmitraService {

    Object verifySsoToken(String token, Long loanId, VerifySsoTokenDTO requestBody) throws Exception;
    Object serviceTransaction(String token, Long loanId, ServiceTransactionDTO requestBody) throws Exception;
    Object updateTransactionPosting(String token, Long loanId, UpdateTransactionPostingDTO requestBody) throws Exception;
    Object verifyTransaction(String token, Long loanId, VerifyTransactionDTO requestBody) throws Exception;
    Object getKioskDetails(String token, Long loanId, GetKioskDetailsDTO requestBody) throws Exception;

}
