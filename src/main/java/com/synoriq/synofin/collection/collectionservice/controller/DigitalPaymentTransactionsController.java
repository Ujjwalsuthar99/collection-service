package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.DigitalPaymentTransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class DigitalPaymentTransactionsController {


    public DigitalPaymentTransactionsController (DigitalPaymentTransactionsService digitalPaymentTransactionsService) {
        this.digitalPaymentTransactionsService = digitalPaymentTransactionsService;
    }
    private final DigitalPaymentTransactionsService digitalPaymentTransactionsService;

    @RequestMapping(value = "digital-payment-transactions/transactions/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDigitalPaymentTransactionsUserWise(@RequestHeader("Authorization") String bearerToken, @PathVariable("userId") Long userId,
                                                                        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                        @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                                        @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
                                                                        @RequestParam(value = "searchKey", defaultValue = "") String searchKey) {

        BaseDTOResponse<Object> baseResponse;
        Object digitalPaymentTransactionsResponse;
        ResponseEntity<Object> response;

        try {
            digitalPaymentTransactionsResponse = digitalPaymentTransactionsService.getDigitalPaymentTransactionsUserWise(userId, page, size, fromDate, toDate, searchKey);
            response = new ResponseEntity<>(digitalPaymentTransactionsResponse, HttpStatus.OK);


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

    @RequestMapping(value = "transaction-status-check", method = RequestMethod.POST)
    public ResponseEntity<Object> checkDigitalPaymentStatus(@RequestHeader("Authorization") String token, @RequestBody CommonTransactionStatusCheckRequestDTO reqBody) throws Exception {
        Object result = digitalPaymentTransactionsService.checkDigitalPaymentStatus(token, reqBody);
        log.info("result printing -> {}", result);
        return new ResponseEntity<>(new BaseDTOResponse<>(result), HttpStatus.OK);
    }
}

