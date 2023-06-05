package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
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
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
public class ReceiptTransferController {

    @Autowired
    ReceiptTransferService receiptTransferService;

    @RequestMapping(value = "/receipt-transfer/generate", method = RequestMethod.POST)
    public ResponseEntity<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, @RequestHeader("Authorization") String bearerToken) {
//        log.info("my request body {}", receiptTransferDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse result = receiptTransferService.createReceiptTransfer(receiptTransferDtoRequest, bearerToken);
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
    public ResponseEntity<Object> getReceiptTransferSummary(@RequestBody ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, @RequestHeader("Authorization") String bearerToken) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferEntity result;
        try {
            result = receiptTransferService.statusUpdate(receiptTransferStatusUpdateDtoRequest, bearerToken);
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
    public ResponseEntity<Object> getReceiptTransferById(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptTransferId") Long receiptTransferId, @RequestParam("userId") Long userId) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferResponseDTO result;
        try {
            log.info("Receipt Transfer id {}", receiptTransferId);
            result = receiptTransferService.getReceiptTransferById(bearerToken, receiptTransferId, userId);
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
                                                             @RequestParam("status") String status,
                                                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<Map<String, Object>> result;
        try {
            log.info("Receipt Transfer user id {}", transferredBy);
            result = receiptTransferService.getReceiptTransferByUserId(transferredBy, fromDate, toDate, status, pageNo, pageSize);
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

    @RequestMapping(value = "/users/{transferredBy}/all-receipt-transfer", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptTransferByUserIdWithAllStatus(@PathVariable("transferredBy") Long transferredBy,
                                                             @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                             @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
                                                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        Map<String, List<Map<String, Object>>> result;
        try {
            log.info("Receipt Transfer user id {}", transferredBy);
            result = receiptTransferService.getReceiptTransferByUserIdWithAllStatus(transferredBy, fromDate, toDate, pageNo, pageSize);
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

    @RequestMapping(value = "/receipt-transfer/receipt-id/{receiptId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptTransferByReceiptId(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptId") Long receiptId) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferDataByReceiptIdResponseDTO result;
        try {
            log.info("Receipt id {}", receiptId);
            result = receiptTransferService.getReceiptTransferByReceiptId(bearerToken, receiptId);
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
