package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs.GetDocumentsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class UtilityController {

    @Autowired
    UtilityService utilityService;

    @RequestMapping(value = "getMasterType", method = RequestMethod.POST)
    public ResponseEntity<Object> getMasterData(@RequestHeader("Authorization") String bearerToken, @RequestBody MasterDtoRequest masterDtoRequest) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object masterResponse;
        ResponseEntity<Object> response = null;

        try {
            masterResponse = utilityService.getMasterData(bearerToken, masterDtoRequest);
            response = new ResponseEntity<>(masterResponse, HttpStatus.OK);
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

    @RequestMapping(value = "getAllUserData", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllUserDetail(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                   @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                   @RequestParam(value = "key", defaultValue = "", required = false) String key) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object userResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getUserDetail(bearerToken, page, size, key);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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

    @RequestMapping(value = "getContactSupport", method = RequestMethod.GET)
    public ResponseEntity<Object> getContactSupport(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "keyword") String keyword, @RequestParam(value = "model") String model) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object userResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getContactSupport(bearerToken, keyword, model);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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

    @RequestMapping(value = "getBankNameByIFSC", method = RequestMethod.GET)
    public ResponseEntity<Object> getBankNameByIFSC(@RequestParam(value = "keyword") String keyword) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getBankNameByIFSC(keyword);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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


    @RequestMapping(value = "uploadImageOnS3", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadImageOnS3(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile imageData,
                                                  @RequestParam("user_ref_no") String userRefNo,
                                                  @RequestParam("file_name") String fileName,
                                                  @RequestParam("latitude") String latitude,
                                                  @RequestParam("longitude") String longitude) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            result = utilityService.uploadImageOnS3(token, imageData, userRefNo, fileName, latitude, longitude);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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


    @RequestMapping(value = "sendPdfToCustomerUsingS3", method = RequestMethod.POST)
    public ResponseEntity<Object> sendPdfToCustomerUsingS3(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile imageData,
                                                           @RequestParam("user_ref_no") String userRefNo,
                                                           @RequestParam("client_id") String clientId,
                                                           @RequestParam("payment_mode") String paymentMode,
                                                           @RequestParam("receipt_amount") String receiptAmount,
                                                           @RequestParam("file_name") String fileName,
                                                           @RequestParam("user_id") String userId,
                                                           @RequestParam("customer_type") String customerType,
                                                           @RequestParam("customer_name") String customerName,
                                                           @RequestParam("applicant_mobile_number") String applicantMobileNumber,
                                                           @RequestParam("collected_from_number") String collectedFromMobileNumber,
                                                           @RequestParam("loan_number") String loanNumber) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            result = utilityService.sendPdfToCustomerUsingS3(token, imageData, userRefNo, clientId, paymentMode, receiptAmount, fileName, userId, customerType, customerName, applicantMobileNumber, collectedFromMobileNumber, loanNumber);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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

    @RequestMapping(value = "downloadBase64FromS3", method = RequestMethod.GET)
    public ResponseEntity<Object> downloadBase64FromS3(@RequestHeader("Authorization") String token,
                                                       @RequestParam("file_name") String fileName,
                                                       @RequestParam("user_ref_no") String userRefNo,
                                                       @RequestParam(value = "isNativeFolder", defaultValue = "true", required = false) boolean isNativeFolder,
                                                       @RequestParam(value = "isCustomerPhotos", defaultValue = "false", required = false) boolean isCustomerPhotos) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DownloadBase64FromS3ResponseDTO result;

        try {
            result = utilityService.downloadBase64FromS3(token, userRefNo, fileName, isNativeFolder, isCustomerPhotos);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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


    @GetMapping("/getThermalPrintData")
    public ResponseEntity<Object> getThermalPrintData(@RequestHeader("Authorization") String token, @RequestParam("receiptId") String receiptId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getThermalPrintData(receiptId);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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

    @RequestMapping(value = "ocr-check", method = RequestMethod.POST)
    public ResponseEntity<Object> ocrCheck(@RequestHeader("Authorization") String token, @RequestBody OcrCheckRequestDTO reqBody) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        OcrCheckResponseDTO result;

        try {
            result = utilityService.ocrCheck(token, reqBody);
            if (result.getData() == null && result.getError() != null) {
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            } else {
                response = new ResponseEntity<>(result, HttpStatus.OK);
            }
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

    @RequestMapping(value = "getDocuments", method = RequestMethod.POST)
    public ResponseEntity<Object> getDocuments(@RequestHeader("Authorization") String token, @RequestParam("loanId") String loanId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = utilityService.getDocuments(token, loanId);
            if (baseResponse.getData() == null && baseResponse.getError() != null) {
                response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
            } else {
                response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            }
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
