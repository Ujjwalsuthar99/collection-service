package com.synoriq.synofin.collection.collectionservice.rest.request.consumedapilogdtos;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import lombok.Data;

@Data
public class ConsumedApiLogRequestDTO {
    Long createdBy;
    EnumSQLConstants.LogNames logName;
    Long loanId;
    String apiType;
    Object requestBody;
    Object responseData;
    String responseStatus;
    String endPoint;


    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public EnumSQLConstants.LogNames getLogName() {
        return logName;
    }

    public void setLogName(EnumSQLConstants.LogNames logName) {
        this.logName = EnumSQLConstants.LogNames.valueOf(String.valueOf(logName));
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
