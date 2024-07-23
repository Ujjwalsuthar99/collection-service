package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.Date;
import java.util.Map;

public interface FollowUpService {

    public BaseDTOResponse<Object> getFollowupById(Long followupById) throws Exception;
    public Map<String, Object> getFollowupDetailsById(Long followupId) throws Exception;
    public BaseDTOResponse<Object> getFollowupLoanWiseWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date toDate) throws Exception;
    public BaseDTOResponse<Object> getFollowupUserWiseWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date toDate, String searchKey) throws Exception;
    public BaseDTOResponse<Object> createFollowup(FollowUpDtoRequest followUpDtoRequest, String token) throws Exception;
    public BaseDTOResponse<Object> updateStatus(FollowUpStatusRequestDTO followUpDtoRequest, String token) throws Exception;


}