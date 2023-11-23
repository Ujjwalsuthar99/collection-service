package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ReceiptController {

    @Autowired
    ReceiptService receiptService;

    @RequestMapping(value = "/users/{userName}/receipts", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptsByUserIdWithDuration(@PathVariable(value = "userName") String userName,
                                                                  @RequestParam(value = "searchKey", defaultValue = "", required = false) String searchKey,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByUserIdWithDuration(userName, fromDate, toDate, searchKey, pageNo, pageSize);
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

    @RequestMapping(value = "/users/{userName}/receipts-not-transferred", method = RequestMethod.GET)
    public ResponseEntity<Object> getReceiptsByUserIdWhichNotTransferred(@PathVariable(value = "userName") String userName) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByUserIdWhichNotTransferred(userName);
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

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        ServiceRequestSaveResponse createReceiptResponse;

        try {
            createReceiptResponse = receiptService.createReceipt(receiptServiceDtoRequest, bearerToken);
            if (createReceiptResponse.getData() == null && createReceiptResponse.getError() != null) {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.BAD_REQUEST);
            } else if (createReceiptResponse.getData().getServiceRequestId() == null && createReceiptResponse.getError() != null) {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.BAD_REQUEST);
            } else  {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.OK);
            }

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

    @RequestMapping(value = "/get-pdf", method = RequestMethod.GET)
    public ResponseEntity<Object> getPdf(@RequestHeader("Authorization") String bearerToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        ResponseEntity<Object> responseEntity = null;
        BaseDTOResponse<Object> response = null;
        try {
//            log.info("token {}", bearerToken);
//            log.info("deliverableType {}", deliverableType);
//            log.info("serviceRequestId {}", serviceRequestId);
            receiptService.getPdf(bearerToken, httpServletRequest, httpServletResponse);
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ->  {}", ee.getMessage());
            if (ErrorCode.getErrorCode(1017004) == null) {
                responseEntity = new ResponseEntity<>(ErrorCode.getErrorCode(1017004), HttpStatus.BAD_REQUEST);

//                throw new CommonServiceException(ErrorCodes.getErrorCode("INTERNAL_SERVER_ERROR"));
            } else {
                responseEntity = new ResponseEntity<>(ErrorCode.getErrorCode(1017002), HttpStatus.BAD_REQUEST);
//                throw new CommonServiceException(ErrorCodes.getErrorCode(ee.getMessage().trim()));
            }
        }
        return responseEntity;

    }
}
