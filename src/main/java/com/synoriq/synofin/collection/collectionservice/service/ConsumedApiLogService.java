package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;

public interface ConsumedApiLogService {
    public void createConsumedApiLog(EnumSQLConstants.LogNames logName, Long userId, Object requestBody, Object responseBody, String responseStatus, Long loanId, String apiType, String endPoint);

}
