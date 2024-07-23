package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.lms.commondto.rest.constants.ErrorCode;
import com.synoriq.synofin.lms.commondto.rest.response.ErrorPayLoad;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BaseDTOResponse<T> {


    private Boolean response = false;
    private T data;
    private Object error;

    public BaseDTOResponse(T data) {
        this.response = true;
        this.data = data;
    }

    public BaseDTOResponse(ErrorCode errorCode) {
        this.response = false;
        this.data = null;
        this.error = new ErrorPayLoad(errorCode);
    }

    public BaseDTOResponse(List<ErrorCode> errorCodeList) {
        this.response = false;
        this.data = null;
        List<String> errorCodes = new ArrayList<>();
        for (ErrorCode errorCode : errorCodeList) {
            errorCodes.add(errorCode.getResponseMessage());
        }
        error = errorCodes;
    }

    public BaseDTOResponse(ErrorCode errorCode, String errorMessage) {
        this.response = false;
        this.data = null;
        this.error = new ErrorPayLoad(errorCode, errorMessage);
    }

    public BaseDTOResponse(ErrorCode errorCode, Boolean response) {
        this.response = response;
        if (response) {
            this.data = (T) new ErrorPayLoad(errorCode);
            this.error = null;
        } else {
            this.data = null;
            this.error = new ErrorPayLoad(errorCode);
        }
    }

    public BaseDTOResponse(String errorMsg, Integer errorCode) {
        this.response = false;
        this.data = null;
        Map<String, Object> error = new HashMap<>();
        error.put("code", errorCode);
        error.put("text", errorMsg);
        this.error = error;
    }

    public BaseDTOResponse(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode errorCode) {
        this.response = false;
        this.data = null;
        this.error = new com.synoriq.synofin.collection.collectionservice.rest.response.ErrorPayLoad(errorCode);
    }

    public BaseDTOResponse(Boolean response, T data, Object error) {
        this.response = response;
        this.data = data;
        this.error = error;
    }
}
