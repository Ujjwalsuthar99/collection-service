package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos.DynamicQrCodeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface QrCodeService {
    DynamicQrCodeResponseDTO sendQrCodeNew(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws  ConnectorException, JsonProcessingException, InterruptedException;
    Object getQrCodeTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws CustomException, ConnectorException, JsonProcessingException, InterruptedException;
    Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws CustomException, InterruptedException;
    Object qrStatusCheck(String token, String merchantId) throws CustomException;

}
