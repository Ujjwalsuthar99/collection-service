package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.List;

public interface AdditionalContactDetailsService {

    public List<AdditionalContactDetailsDtoRequest> getAdditionalContactDetailsByLoanId(Long loanId) throws Exception;
    public AdditionalContactDetailsDtoRequest getAdditionalContactDetailsById(Long additionalContactDetailId) throws Exception;
    public BaseDTOResponse<Object> createAdditionalContactDetail(AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) throws Exception;


}
