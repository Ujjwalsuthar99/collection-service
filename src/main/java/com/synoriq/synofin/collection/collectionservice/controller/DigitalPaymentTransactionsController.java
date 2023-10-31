package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.DigitalPaymentTransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class DigitalPaymentTransactionsController {

    @Autowired

    DigitalPaymentTransactionsService digitalPaymentTransactionsService;

    @RequestMapping(value = "digital-payment-transactions/transactions/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDigitalPaymentTransactionsUserWise(@RequestHeader("Authorization") String bearerToken, @PathVariable("userId") String userId,
                                                                        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size) {

        BaseDTOResponse<Object> baseResponse;
        Object digitalPaymentTransactionsResponse;
        ResponseEntity<Object> response;

        try {
            digitalPaymentTransactionsResponse = digitalPaymentTransactionsService.getDigitalPaymentTransactionsUserWise(bearerToken, userId, page, size);
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
}

