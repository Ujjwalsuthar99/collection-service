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
    public ResponseEntity<Object> dataNotFoundException(Exception e) throws Exception {
        BaseDTOResponse<Object> errResponse;
        if (e.getMessage().matches("\\d+") && ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
            errResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
        } else {
            errResponse = new BaseDTOResponse<>(e.getMessage(), 999999);
        }
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectorException.class)
    public ResponseEntity<Object> connectorException(ConnectorException e) throws Exception {
        log.info("connector error : {}", e.getMessage());
        IntegrationServiceErrorResponseDTO r = new ObjectMapper().readValue(e.getMessage(), IntegrationServiceErrorResponseDTO.class);
        return new ResponseEntity<>(new BaseDTOResponse<>(false, null, r), HttpStatus.BAD_REQUEST);

    }
}