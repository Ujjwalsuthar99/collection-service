package com.synoriq.synofin.collection.collectionservice.common.exception;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -1307493420921168255L;
    private final Integer code;
    private String text = "";
    private HttpStatus httpStatus;

    public CustomException(String str, Integer code) {
        super(str);
        this.code = code;
    }

    public CustomException(String str) {
        super(str);
        this.code = 99999;
        this.text = str;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomException(String str, HttpStatus httpStatus) {
        super(str);
        this.code = 99999;
        this.httpStatus = httpStatus;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.httpStatus = httpStatus;
    }

    public CustomException(ErrorCode errorCode, String str) {
        super(!str.isEmpty() ? str : errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.text = !str.isEmpty() ? str : errorCode.getResponseMessage();
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomException(ErrorCode errorCode, String str, HttpStatus httpStatus) {
        super(!str.isEmpty() ? str : errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.text = !str.isEmpty() ? str : errorCode.getResponseMessage();
        this.httpStatus = httpStatus;
    }

}

