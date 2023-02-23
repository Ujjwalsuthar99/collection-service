package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
public class ReceiptServiceController {

    @Autowired
    ReceiptService receiptService;


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


}






