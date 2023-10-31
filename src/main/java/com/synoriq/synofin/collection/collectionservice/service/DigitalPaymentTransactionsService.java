package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;

public interface DigitalPaymentTransactionsService {

    public Object getDigitalPaymentTransactionsUserWise(String token, String userId, Integer page, Integer size) throws Exception;

}
