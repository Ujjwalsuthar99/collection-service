package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;

import java.util.Date;

public interface DigitalPaymentTransactionsService {

    Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws Exception;

    Object checkDigitalPaymentStatus(String token, CommonTransactionStatusCheckRequestDTO object) throws Exception;

}
