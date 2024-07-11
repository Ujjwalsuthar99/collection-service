package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;

public interface ConsumedApiLogService {
    public void createConsumedApiLog(EnumSQLConstants.LogNames logName, Long userId, Object requestBody, Object responseBody, String responseStatus, Long loanId, String apiType, String endPoint);

    ConsumedApiLogsEntity getLastDataByLoanIdAndLogName(Long loanId, EnumSQLConstants.LogNames logName);
}
