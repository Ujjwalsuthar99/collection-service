package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;

public interface QrCodeService {

    public DynamicQrCodeResponseDTO sendQrCode(String token, DynamicQrCodeRequestDTO requestBody) throws Exception;
    public DynamicQrCodeCheckStatusResponseDTO getQrCodeTransactionStatus(String token, DynamicQrCodeStatusCheckRequestDTO requestBody) throws Exception;
    public Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws Exception;
    public Object qrStatusCheck(String token, String merchantId) throws Exception;

}
