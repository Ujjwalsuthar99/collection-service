package com.synoriq.synofin.collection.collectionservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        BaseDTOResponse<Object> baseResponse = new BaseDTOResponse<>(errors);
        return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> dataNotFoundException(Exception e) {
        BaseDTOResponse<Object> errResponse;
        if (e.getMessage().matches("\\d+") && ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
            errResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
        } else {
            errResponse = new BaseDTOResponse<>(e.getMessage(), 999999);
        }
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectorException.class)
    public ResponseEntity<Object> connectorException(ConnectorException e) {
        log.info("connector error : {}", e.getMessage());
        IntegrationServiceErrorResponseDTO integrationServiceErrorResponseDTO = IntegrationServiceErrorResponseDTO.builder().code(String.valueOf(e.getCode())).message(e.getMessage()).build();
        return new ResponseEntity<>(new BaseDTOResponse<>(false, null, integrationServiceErrorResponseDTO), e.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customException(CustomException ex) {
        log.error("Custom Error : {} ", ex);
//        BaseResponse<Object> errResponse = new BaseResponse<>(ErrorCode.getErrorCode(ex.getCode()));
        BaseDTOResponse<Object> errResponse = null;
        if (ex.getCode() != null && ErrorCode.getErrorCode(ex.getCode()) != null) {
            errResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(ex.getCode()));
        } else {
            errResponse = new BaseDTOResponse<>(ex.getMessage(), ex.getCode());
        }
        HttpStatus httpStatus = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(errResponse, httpStatus);
    }
}