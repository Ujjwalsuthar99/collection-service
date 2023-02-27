package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ReceiptController {

    @Autowired
    ReceiptService receiptService;

    @RequestMapping(value = "/users/{userId}/receipts", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptsByUserIdWithDuration(@PathVariable(value = "userId") Long userId,
                                                                  @RequestParam(value = "status", required = false) String status,
                                                                  @RequestParam(value = "paymentmode", required = false) String paymentMode,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByUserIdWithDuration(userId, fromDate, toDate, status, paymentMode);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/users/{userId}/receipts-not-transferred", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptsByUserIdWhichNotTransferred(@PathVariable(value = "userId") Long userId,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByUserIdWhichNotTransferred(userId, fromDate, toDate);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @RequestMapping(value = "/loans/{loanId}/receipts", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptsByLoanIdWithDuration(@PathVariable(value = "loanId") Long loanId,
                                                                  @RequestParam(value = "status", required = false) String status,
                                                                  @RequestParam(value = "paymentmode", required = false) String paymentMode,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByLoanIdWithDuration(loanId, fromDate, toDate, status, paymentMode);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @RequestMapping(value = "/create-receipt", method = RequestMethod.POST)
    public ResponseEntity<Object> createReceipt(@RequestBody ReceiptServiceDtoRequest receiptServiceDtoRequest, @RequestHeader("Authorization") String bearerToken) {
        log.info("my request body {}", receiptServiceDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object createReceiptResponse;

        try {
            createReceiptResponse = receiptService.createReceipt(receiptServiceDtoRequest, bearerToken);
            response = new ResponseEntity<>(createReceiptResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @RequestMapping(value = "/get-receipt-date", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptDate(@RequestHeader("Authorization") String bearerToken) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object getReceiptDateResponse;

        try {
            getReceiptDateResponse = receiptService.getReceiptDate(bearerToken);
            response = new ResponseEntity<>(getReceiptDateResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }
}
