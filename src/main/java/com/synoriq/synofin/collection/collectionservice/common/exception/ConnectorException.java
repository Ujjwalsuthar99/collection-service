package com.synoriq.synofin.collection.collectionservice.common.exception;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ConnectorException extends RuntimeException {

    private static final long serialVersionUID = -1307493420921168255L;
    private final Integer code;
    private final String text;
    private final HttpStatus httpStatus;
    private final String requestId;

    public ConnectorException(String str, Integer code) {
        super(str);
        this.code = code;
        this.text = "";
        this.httpStatus = null;
        this.requestId = "";
    }

    public ConnectorException(String str) {
        super(str);
        this.code = ErrorCode.DEFAULT_ERROR_CODE.getCodeValue();
        this.text = "";
        this.httpStatus = null;
        this.requestId = "";
    }

    public ConnectorException(ErrorCode errorCode) {
        super(errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.text = "";
        this.httpStatus = null;
        this.requestId = "";
    }

    public ConnectorException(IntegrationServiceErrorResponseDTO integrationServiceErrorResponseDTO, HttpStatus httpStatus, String requestId) {
        super(!integrationServiceErrorResponseDTO.getMessage().isEmpty() ? integrationServiceErrorResponseDTO.getMessage() : "");
        this.code = Integer.valueOf(integrationServiceErrorResponseDTO.getCode());
        this.text = integrationServiceErrorResponseDTO.getMessage();
        this.httpStatus = httpStatus;
        this.requestId = requestId;
    }

    public ConnectorException(ErrorCode errorCode, String str, HttpStatus httpStatus, String requestId) {
        super(!str.isEmpty() ? str : errorCode.getResponseMessage());
        this.code = errorCode.getCodeValue();
        this.text = !str.isEmpty() ? str : errorCode.getResponseMessage();
        this.httpStatus = httpStatus;
        this.requestId = requestId;
    }
}

