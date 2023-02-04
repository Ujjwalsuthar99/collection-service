package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptTransferService;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
public class ReceiptTransferController {

    @Autowired
    ReceiptTransferService receiptTransferService;

    @RequestMapping(value = "/receipt-transfer/generate", method = RequestMethod.POST)
    public ResponseEntity<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest) {
        log.info("my request body {}", receiptTransferDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse result = receiptTransferService.createReceiptTransfer(receiptTransferDtoRequest);
            baseResponse = new BaseDTOResponse<>(result.getData());
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @RequestMapping(value = "/receipt-transfer/summary/{transferredByUserId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptTransferSummary(@PathVariable("transferredByUserId") Long transferredByUserId) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<ReceiptTransferDTO> result;
        try {
            result = receiptTransferService.getReceiptTransferSummary(transferredByUserId);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get receipt transfer summary details API response success | transferred_by=[{}]", transferredByUserId);
        } catch (Exception e) {
            baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }



    @RequestMapping(value = "/receipt-transfer/status-update", method = RequestMethod.PUT)
    public ResponseEntity<Object> getReceiptTransferSummary(@RequestBody ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferEntity result;
        try {
            result = receiptTransferService.statusUpdate(receiptTransferStatusUpdateDtoRequest);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Status update {}", receiptTransferStatusUpdateDtoRequest.getStatus());
            log.info("Receipt transfer id update {}", receiptTransferStatusUpdateDtoRequest.getReceiptTransferId());
        } catch (Exception e) {
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }



    @RequestMapping(value = "/receipt-transfer/{receiptTransferId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptTransferById(@PathVariable("receiptTransferId") Long receiptTransferId) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferEntity result;
        try {
            log.info("Receipt Transfer id {}", receiptTransferId);
            result = receiptTransferService.getReceiptTransferById(receiptTransferId);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }



    @RequestMapping(value = "/users/{transferredBy}/receipt-transfer", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptTransferByUserId(@PathVariable("transferredBy") Long transferredBy,
                                                             @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                             @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
                                                             @RequestParam("status") String status) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<ReceiptTransferEntity> result;
        try {
            log.info("Receipt Transfer user id {}", transferredBy);
            result = receiptTransferService.getReceiptTransferByUserId(transferredBy, fromDate, toDate, status);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


}
