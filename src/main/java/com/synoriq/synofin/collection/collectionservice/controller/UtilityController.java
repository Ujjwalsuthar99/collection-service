package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.DeleteImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.IntegrationConnectorService;
import com.synoriq.synofin.collection.collectionservice.service.QrCodeService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class UtilityController {

    @Autowired
    UtilityService utilityService;

    @Autowired
    IntegrationConnectorService integrationConnectorService;

    @Autowired
    QrCodeService qrCodeService;

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
                                                  @RequestParam("module") String module,
                                                  @RequestParam("latitude") String latitude,
                                                  @RequestParam("longitude") String longitude) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            result = integrationConnectorService.uploadImageOnS3(token, imageData, module, latitude, longitude, false);
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
                                                           @RequestParam("loan_number") String loanNumber,
                                                           @RequestParam("receipt_id") Long receiptId) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            result = utilityService.sendPdfToCustomerUsingS3(token, imageData, userRefNo, clientId, paymentMode, receiptAmount, fileName, userId, customerType, customerName, applicantMobileNumber, collectedFromMobileNumber, loanNumber, receiptId);
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
            result = integrationConnectorService.downloadBase64FromS3(token, userRefNo, fileName, isNativeFolder, isCustomerPhotos);
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

    @RequestMapping(value = "deleteImageOnS3", method = RequestMethod.GET)
    public ResponseEntity<Object> deleteImageOnS3(@RequestHeader("Authorization") String token,
                                                       @RequestParam("file_name") String fileName,
                                                       @RequestParam("user_ref_no") String userRefNo) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DeleteImageOnS3ResponseDTO result;

        try {
            result = integrationConnectorService.deleteImageOnS3(token, userRefNo, fileName);
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
            result = integrationConnectorService.ocrCheck(token, reqBody);
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

    @RequestMapping(value = "getDocuments", method = RequestMethod.GET)
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

    @RequestMapping(value = "send-qr-code", method = RequestMethod.POST)
    public ResponseEntity<Object> sendQrCode(@RequestHeader("Authorization") String token, @RequestBody DynamicQrCodeRequestDTO reqBody) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DynamicQrCodeResponseDTO result;

        try {
            result = qrCodeService.sendQrCode(token, reqBody);
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

    @RequestMapping(value = "send-qr-code-new", method = RequestMethod.POST)
    public ResponseEntity<Object> sendQrCodeNew(@RequestHeader("Authorization") String token,
                                                @RequestParam("paymentReferenceImage") MultipartFile paymentReferenceImage,
                                                @RequestParam("selfieImage") MultipartFile selfieImage,
                                                @RequestParam("data") Object data) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DynamicQrCodeResponseDTO result;

        try {
            result = qrCodeService.sendQrCodeNew(token, data, paymentReferenceImage, selfieImage);
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

    @RequestMapping(value = "get-qr-code-transaction-status", method = RequestMethod.POST)
    public ResponseEntity<Object> getQrCodeTransactionStatus(@RequestHeader("Authorization") String token, @RequestBody DynamicQrCodeStatusCheckRequestDTO reqBody) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DynamicQrCodeCheckStatusResponseDTO result;

        try {
            result = qrCodeService.getQrCodeTransactionStatus(token, reqBody);
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

    @RequestMapping(value = "qrCodeCallBack", method = RequestMethod.POST)
    public ResponseEntity<Object> qrCodeCallBack(@RequestHeader("Authorization") String token, @RequestBody DynamicQrCodeCallBackRequestDTO reqBody) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = qrCodeService.qrCodeCallBack(token, reqBody);
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

    @RequestMapping(value = "qr-status-check", method = RequestMethod.GET)
    public ResponseEntity<Object> qrStatusCheck(@RequestHeader("Authorization") String token, @RequestParam("merchant_id") String merchantId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = qrCodeService.qrStatusCheck(token, merchantId);
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


    @RequestMapping(value = "send-otp", method = RequestMethod.GET)
    public ResponseEntity<Object> sendOtp(@RequestHeader("Authorization") String token, @RequestParam("mobileNumber") String mobileNumber) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        MasterDTOResponse result;

        try {
            result = integrationConnectorService.sendOtp(token, mobileNumber);
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

    @RequestMapping(value = "verify-otp", method = RequestMethod.GET)
    public ResponseEntity<Object> verifyOtp(@RequestHeader("Authorization") String token, @RequestParam("mobileNumber") String mobileNumber, @RequestParam("otp") String otp) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        MasterDTOResponse result;

        try {
            result = integrationConnectorService.verifyOtp(token, mobileNumber, otp);
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

    @RequestMapping(value = "resend-otp", method = RequestMethod.GET)
    public ResponseEntity<Object> resendOtp(@RequestHeader("Authorization") String token, @RequestParam("mobileNumber") String mobileNumber) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        MasterDTOResponse result;

        try {
            result = integrationConnectorService.resendOtp(token, mobileNumber);
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

    @RequestMapping(value = "get-collaterals", method = RequestMethod.GET)
    public ResponseEntity<Object> getCollaterals(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = utilityService.getCollaterals(loanId, token);
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


    @RequestMapping(value = "mobile-number-validation", method = RequestMethod.GET)
    public ResponseEntity<Object> employeeMobileNumberValidator(@RequestHeader("Authorization") String token, @RequestParam("mobileNumber") String mobileNumber) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = utilityService.employeeMobileNumberValidator(token, mobileNumber);
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
