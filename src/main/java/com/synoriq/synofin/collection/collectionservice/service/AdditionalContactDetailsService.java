package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.List;

public interface AdditionalContactDetailsService {

     List<AdditionalContactDetailsDtoRequest> getAdditionalContactDetailsByLoanId(Long loanId) throws CustomException;
     AdditionalContactDetailsDtoRequest getAdditionalContactDetailsById(Long additionalContactDetailId) throws CollectionException;
     BaseDTOResponse<Object> createAdditionalContactDetail(AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) throws CollectionException;

}
