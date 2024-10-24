package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositinvoicedtos.DepositInvoiceRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferAirtelDepositStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferForAirtelRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.depositinvoiceresponsedtos.DepositInvoiceResponseDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos.AllBankTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptTransferService;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final ReceiptTransferService receiptTransferService;

    public ReceiptTransferController(ReceiptTransferService receiptTransferService) {
        this.receiptTransferService = receiptTransferService;
    }
    @PostMapping(value = "/receipt-transfer/generate")
    public ResponseEntity<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, @RequestHeader("Authorization") String bearerToken) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptTransferService.createReceiptTransfer(receiptTransferDtoRequest, bearerToken);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (CustomException ee) {
            baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(ee.getCode(), ee.getMessage()));
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }

    @PostMapping(value = "/receipt-transfer/generate-new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createReceiptTransferNew(@RequestHeader("Authorization") String bearerToken,
                                                           @RequestParam("transfer_proof") MultipartFile transferProof,
                                                           @RequestParam("data") Object object) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = receiptTransferService.createReceiptTransferNew(object, transferProof, bearerToken);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (CustomException ee) {
            baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(ee.getCode(), ee.getMessage()));
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @GetMapping(value = "/receipt-transfer/summary/{transferredByUserId}")
    public ResponseEntity<Object> getReceiptTransferSummary(@PathVariable("transferredByUserId") Long transferredByUserId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<ReceiptTransferDTO> result;
        try {
            result = receiptTransferService.getReceiptTransferSummary(transferredByUserId);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @PutMapping(value = "/receipt-transfer/status-update")
    public ResponseEntity<Object> getReceiptTransferSummary(@RequestBody ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, @RequestHeader("Authorization") String bearerToken) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferEntity result;
        try {
            result = receiptTransferService.statusUpdate(receiptTransferStatusUpdateDtoRequest, bearerToken);
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


    @GetMapping(value = "/receipt-transfer/{receiptTransferId}")
    public ResponseEntity<Object> getReceiptTransferById(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptTransferId") Long receiptTransferId, @RequestParam("userId") Long userId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        try {
            baseResponse = receiptTransferService.getReceiptTransferById(bearerToken, receiptTransferId, userId);
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


    @GetMapping(value = "/users/{transferredBy}/receipt-transfer")
    public ResponseEntity<Object> getReceiptTransferByUserId(@PathVariable("transferredBy") Long transferredBy,
                                                             @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                             @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
                                                             @RequestParam("status") String status,
                                                             @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize){

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<Map<String, Object>> result;
        try {
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

    @GetMapping(value = "/users/{transferredBy}/all-receipt-transfer")
    public ResponseEntity<Object> getReceiptTransferByUserIdWithAllStatus(@PathVariable("transferredBy") Long transferredBy,
                                                                          @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                                          @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
                                                                          @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                                          @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        Map<String, List<Map<String, Object>>> result;
        try {
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

    @GetMapping(value = "/receipt-transfer/receipt-id/{receiptId}")
    public ResponseEntity<Object> getReceiptTransferByReceiptId(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptId") Long receiptId) throws CollectionException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        ReceiptTransferDataByReceiptIdResponseDTO result;
        try {
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

    @GetMapping(value = "/receipt-transfer/all-bank-transfers")
    public ResponseEntity<Object> getAllBankTransfers(@RequestHeader("Authorization") String bearerToken,
                                                      @RequestParam(value = "searchKey", defaultValue = "", required = false) String searchKey,
                                                      @RequestParam(value = "status", defaultValue = "", required = false) String status,
                                                      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                      @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws CollectionException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        AllBankTransferResponseDTO result;
        try {
            result = receiptTransferService.getAllBankTransfers(bearerToken, searchKey, status, pageNo, pageSize);
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

    @GetMapping(value = "/receipt-transfer/receipts-data/{receiptTransferId}")
    public ResponseEntity<Object> getReceiptsDataByReceiptTransferId(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptTransferId") Long receiptTransferId) throws CollectionException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        Object result;
        try {
            result = receiptTransferService.getReceiptsDataByReceiptTransferId(bearerToken, receiptTransferId);
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

    @PostMapping(value = "/receipt-transfer/deposit-invoice")
    public ResponseEntity<Object> depositInvoice(@RequestHeader("Authorization") String bearerToken, @RequestBody DepositInvoiceRequestDTO depositInvoiceRequestDTO) throws CustomException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        DepositInvoiceResponseDataDTO result;
        try {
            result = receiptTransferService.depositInvoice(bearerToken, depositInvoiceRequestDTO);
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


    @GetMapping(value = "/receipt-transfer/disable-approve/{receiptId}")
    public ResponseEntity<Object> disableApproveButtonInLms(@RequestHeader("Authorization") String bearerToken, @PathVariable("receiptId") Long receiptId) throws CustomException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        try {
            baseResponse = receiptTransferService.disableApproveButtonInLms(bearerToken, receiptId);
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

    @PutMapping(value = "/receipt-transfer/airtel-deposition/status-update")
    public ResponseEntity<Object> airtelDepositStatusUpdate(@RequestHeader("Authorization") String bearerToken, @RequestBody ReceiptTransferAirtelDepositStatusRequestDTO requestBody) throws CustomException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        try {
            baseResponse = receiptTransferService.airtelDepositStatusUpdate(bearerToken, requestBody);
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


    @PostMapping(value = "/receipt-transfer/get-receipt-transfer-for-airtel")
    public ResponseEntity<Object> getReceiptTransferForAirtel(@RequestHeader("Authorization") String bearerToken, @RequestBody ReceiptTransferForAirtelRequestDTO receiptTransferForAirtelRequestDTO) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        try {
            baseResponse = receiptTransferService.getReceiptTransferForAirtel(bearerToken, receiptTransferForAirtelRequestDTO);
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
