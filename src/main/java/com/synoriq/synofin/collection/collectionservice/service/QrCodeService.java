package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface QrCodeService {
    DynamicQrCodeResponseDTO sendQrCodeNew(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws Exception;
    DynamicQrCodeCheckStatusResponseDTO getQrCodeTransactionStatus(String token, DynamicQrCodeStatusCheckRequestDTO requestBody) throws Exception;
    Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws Exception;
    Object qrStatusCheck(String token, String merchantId) throws Exception;

}
