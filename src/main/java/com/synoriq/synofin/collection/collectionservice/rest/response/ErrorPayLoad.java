package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorPayLoad {
    private Integer code;
    private String text;

    public ErrorPayLoad(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode errorCode) {
        this.code = errorCode.getCodeValue();
        this.text = errorCode.getResponseMessage();
    }

    public ErrorPayLoad(ErrorCode errorCode, String errorResponseMessage) {
        this.code = errorCode.getCodeValue();
        this.text = errorResponseMessage;
    }
}
