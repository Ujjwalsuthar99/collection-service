package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs.ActivityLogBaseResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;

import java.util.Date;

public interface ActivityLogService {

    public BaseDTOResponse<Object> getActivityLogsById(Long activityLogsId) throws Exception;
    public BaseDTOResponse<Object> getActivityLogsByUserIdWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date endDate) throws Exception;
    public ActivityLogBaseResponseDTO getActivityLogsByLoanIdWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date endDate, String filterBy) throws Exception;
    public Long createActivityLogs(CollectionActivityLogDTO activityLogRequest, String token) throws Exception;


}
