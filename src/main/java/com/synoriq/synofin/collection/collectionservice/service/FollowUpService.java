package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.followupdtos.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.followupdtos.FollowUpStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.Date;
import java.util.Map;

public interface FollowUpService {

     BaseDTOResponse<Object> getFollowupById(Long followupById) throws CollectionException;
     Map<String, Object> getFollowupDetailsById(Long followupId) throws CollectionException;
     BaseDTOResponse<Object> getFollowupLoanWiseWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date toDate) throws CustomException;
     BaseDTOResponse<Object> getFollowupUserWiseWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date toDate, String searchKey) throws CustomException;
     BaseDTOResponse<Object> createFollowup(FollowUpDtoRequest followUpDtoRequest, String token) throws CustomException;
     BaseDTOResponse<Object> updateStatus(FollowUpStatusRequestDTO followUpDtoRequest, String token) throws CollectionException;


}
