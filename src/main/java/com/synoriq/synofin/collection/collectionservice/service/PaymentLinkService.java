package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.concurrent.ExecutionException;

import org.springframework.web.multipart.MultipartFile;

public interface PaymentLinkService {

    BaseDTOResponse<Object> sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws ConnectorException, JsonProcessingException, InterruptedException, ExecutionException;

    Object getPaymentTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws CustomException, JsonProcessingException, InterruptedException;

}
