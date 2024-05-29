package com.synoriq.synofin.collection.collectionservice.common.exception;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import lombok.Data;

@Data
public class ConnectorException extends RuntimeException {

    private static final long serialVersionUID = -1307493420921168255L;
    private final Integer code;

    public ConnectorException(String str, Integer code) {
        super(str);
        this.code = code;
    }

    public ConnectorException(String str) {
        super(str);
        this.code = ErrorCode.DEFAULT_ERROR_CODE.getCodeValue();
    }

    public ConnectorException(ErrorCode errorCode) {
        super(errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
    }

}

