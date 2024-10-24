package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferLmsFilterDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }
    @GetMapping(value = "/users/{userName}/receipts")
    public ResponseEntity<Object> getReceiptsByUserIdWithDuration(@PathVariable(value = "userName") String userName,
                                                                  @RequestParam(value = "searchKey", defaultValue = "", required = false) String searchKey,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize){
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

    @GetMapping(value = "/users/{userName}/receipts-not-transferred")
    public ResponseEntity<Object> getReceiptsByUserIdWhichNotTransferred(@PathVariable(value = "userName") String userName){
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


    @GetMapping(value = "/loans/{loanId}/receipts")
    public ResponseEntity<Object> getReceiptsByLoanIdWithDuration(@PathVariable(value = "loanId") Long loanId,
                                                                  @RequestParam(value = "status", required = false) String status,
                                                                  @RequestParam(value = "paymentmode", required = false) String paymentMode,
                                                                  @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate) {
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

    @PostMapping(value = "/create-receipt-new")
    public ResponseEntity<Object> createReceiptNew(@RequestHeader("Authorization") String bearerToken,
                                                   @RequestParam("paymentReferenceImage") MultipartFile paymentReferenceImage,
                                                   @RequestParam("selfieImage") MultipartFile selfieImage,
                                                   @RequestParam("data") Object object) throws Exception{

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        ServiceRequestSaveResponse createReceiptResponse;

        try {

            createReceiptResponse = receiptService.createReceiptNew(object, paymentReferenceImage, selfieImage, bearerToken, false);
            if (createReceiptResponse.getData() == null && createReceiptResponse.getError() == null) {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.BAD_REQUEST);
            } else if (createReceiptResponse.getData() == null) {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.BAD_REQUEST);
            } else if (createReceiptResponse.getData().getServiceRequestId() == null && createReceiptResponse.getError() != null) {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.BAD_REQUEST);
            } else  {
                response = new ResponseEntity<>(createReceiptResponse, HttpStatus.OK);
            }

        } catch(InterruptedException ie){
            log.error("Interrupted Exception Error {}", ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.info("Exception", e);
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @GetMapping(value = "/get-receipt-date")
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

    @GetMapping(value = "/get-pdf")
    public ResponseEntity<Object> getPdf(@RequestHeader("Authorization") String bearerToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        ResponseEntity<Object> responseEntity = null;
        try {
            receiptService.getPdf(bearerToken, httpServletRequest, httpServletResponse);
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ->  {}", ee.getMessage());
            if (ErrorCode.getErrorCode(1017004) == null) {
                responseEntity = new ResponseEntity<>(ErrorCode.getErrorCode(1017004), HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity<>(ErrorCode.getErrorCode(1017002), HttpStatus.BAD_REQUEST);
            }
        }
        return responseEntity;

    }


    @PostMapping(value = "/receipts/receipts-not-transferred-portal")
    public ResponseEntity<Object> getReceiptsByUserIdWhichNotTransferredForPortal(@RequestBody ReceiptTransferLmsFilterDTO filterDTO) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptService.getReceiptsByUserIdWhichNotTransferredForPortal(filterDTO);
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

    @PostMapping("create-collection-receipt")
    public ResponseEntity<Object> createCollectionReceipt(@RequestBody Map<String, Object> requestBody, @RequestHeader("Authorization") String token) throws CustomException {
        Object result = receiptService.createCollectionReceipt(requestBody, token);
        return new ResponseEntity<>(new BaseDTOResponse<>(result), HttpStatus.OK);
    }
}
