package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogBaseResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;

import java.util.Date;
import java.util.List;

public interface ActivityLogService {

     BaseDTOResponse<Object> getActivityLogsById(Long activityLogsId) throws CollectionException;
     BaseDTOResponse<Object> getActivityLogsByUserIdWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date endDate) throws CustomException;
     ActivityLogBaseResponseDTO getActivityLogsByLoanIdWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date endDate, String filterBy) throws CustomException;
     Long createActivityLogs(CollectionActivityLogDTO activityLogRequest, String token) throws CustomException;
     List<CollectionActivityLogsEntity> getActivityLogsByReferenceId(Long referenceId) throws CustomException;


}
