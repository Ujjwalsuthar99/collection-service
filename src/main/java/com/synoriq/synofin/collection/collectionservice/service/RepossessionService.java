package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface RepossessionService {

    public BaseDTOResponse<Object> getRepossessionData(Long loanId) throws Exception;
    public BaseDTOResponse<Object> initiateRepossession(String token ,RepossessionRequestDTO requestDto) throws Exception;
}
