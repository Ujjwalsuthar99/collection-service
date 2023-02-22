package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.CollectionConfigurationService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

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
}
