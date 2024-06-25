package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentLinkService {

    BaseDTOResponse<Object> sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws Exception;

    Object getPaymentTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws Exception;

}
