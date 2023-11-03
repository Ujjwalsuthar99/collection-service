package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;

import java.util.Date;

public interface DigitalPaymentTransactionsService {

    public Object getDigitalPaymentTransactionsUserWise(Long userId, Integer page, Integer size, Date fromDate, Date toDate, String searchKey) throws Exception;

}
