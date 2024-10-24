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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";

    private <T extends Exception> void printDetailedMessage(T e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().startsWith("java.") &&
                    !element.getClassName().startsWith("javax.") &&
                    !element.getClassName().startsWith("org.springframework.")) {
                final String ERR_TYPE_STRING = String.format("Error type : %s %s %s", ANSI_RED, e.getClass().getCanonicalName(), ANSI_RESET);
                final String CLASS_STRING = String.format("Exception in class : %s %s %s", ANSI_GREEN, element.getClassName(), ANSI_RESET);
                final String LINE_NO_STRING = String.format("at line number : %s %s %s", ANSI_BLUE, element.getLineNumber(), ANSI_RESET);
                final String METHOD_STRING = String.format("method : %s %s %s", ANSI_YELLOW, element.getMethodName(), ANSI_RESET);
                final String MSG_STRING = String.format("with message : %s %s %s", ANSI_RED, e.getMessage(), ANSI_RESET);
                log.error("\n\n\t{} \n\t{} \n\t{} \n\t{} \n\t{}\n\n", ERR_TYPE_STRING, CLASS_STRING, LINE_NO_STRING, METHOD_STRING, MSG_STRING);
                break;
            }
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        printDetailedMessage(ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        BaseDTOResponse<Object> baseResponse = new BaseDTOResponse<>(errors);
        return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> dataNotFoundException(Exception e) {
        printDetailedMessage(e);
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
        printDetailedMessage(e);
        log.info("connector error : {}", e.getMessage());
        IntegrationServiceErrorResponseDTO integrationServiceErrorResponseDTO = IntegrationServiceErrorResponseDTO.builder().code(String.valueOf(e.getCode())).message(e.getMessage()).build();
        return new ResponseEntity<>(new BaseDTOResponse<>(false, null, integrationServiceErrorResponseDTO), e.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customException(CustomException ex) {
        printDetailedMessage(ex);
        log.error("Custom Error : {} ", ex);
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