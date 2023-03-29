package com.synoriq.synofin.collection.collectionservice.common.exception;

import com.synoriq.synofin.lms.commondto.rest.constants.ErrorCode;
import lombok.Data;

@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -1307493420921168255L;
    private final Integer code;

    public CustomException(String str, Integer code) {
        super(str);
        this.code = code;
    }

    public CustomException(String str) {
        super(str);
        this.code = ErrorCode.DEFAULT_ERROR_CODE.getCodeValue();
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
    }
}

