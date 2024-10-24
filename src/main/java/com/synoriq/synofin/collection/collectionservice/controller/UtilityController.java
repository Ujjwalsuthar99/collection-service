package com.synoriq.synofin.collection.collectionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.config.DatabaseContextHolder;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.AuthorizationResponse;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.collectionincentivedtos.CollectionIncentiveRequestDTOs;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterdtos.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ocrcheckresponsedtos.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.DeleteImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.downloads3base64dtos.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.IntegrationConnectorService;
import com.synoriq.synofin.collection.collectionservice.service.PaymentLinkService;
import com.synoriq.synofin.collection.collectionservice.service.QrCodeService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.performancemonitoringservice.annotation.TimedAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
@Validated
public class UtilityController {
    public UtilityController(UtilityService utilityService,
                             IntegrationConnectorService integrationConnectorService,
                             PaymentLinkService paymentLinkService, QrCodeService qrCodeService,CollectionConfigurationsRepository collectionConfigurationsRepository) {
        this.utilityService = utilityService;
        this.paymentLinkService = paymentLinkService;
        this.integrationConnectorService = integrationConnectorService;
        this.qrCodeService = qrCodeService;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
    }
    public final UtilityService utilityService;
    public final IntegrationConnectorService integrationConnectorService;
    public final PaymentLinkService paymentLinkService;
    public final QrCodeService qrCodeService;

    public final CollectionConfigurationsRepository collectionConfigurationsRepository;




    @Value("${spring.profiles.active}")
    private String springProfile;

    @PostMapping(value = "getMasterType")
    public ResponseEntity<Object> getMasterData(@RequestHeader("Authorization") String bearerToken, @RequestBody MasterDtoRequest masterDtoRequest) {
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

    @GetMapping(value = "getAllUserData")
    public ResponseEntity<Object> getAllUserDetail(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                   @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                   @RequestParam(value = "key", defaultValue = "", required = false) String key) {
        BaseDTOResponse<Object> baseResponse;
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

    @GetMapping(value = "getContactSupport")
    public ResponseEntity<Object> getContactSupport(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "keyword") String keyword, @RequestParam(value = "model") String model) {
        BaseDTOResponse<Object> baseResponse;
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

    @GetMapping(value = "getBankNameByIFSC")
    public ResponseEntity<Object> getBankNameByIFSC(@RequestParam(value = "keyword") String keyword) {
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


    @PostMapping(value = "uploadImageOnS3")
    public ResponseEntity<Object> uploadImageOnS3(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile imageData,
                                                  @RequestParam("module") String module,
                                                  @RequestParam("latitude") String latitude,
                                                  @RequestParam("longitude") String longitude) throws CustomException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            GeoLocationDTO geoLocationDT = GeoLocationDTO.builder().longitude(longitude).latitude(latitude).build();
            result = integrationConnectorService.uploadImageOnS3(token, imageData, module, geoLocationDT, "");
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


    @PostMapping(value = "sendPdfToCustomerUsingS3")
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
                                                           @RequestParam("receipt_id") Long receiptId) {
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

    @GetMapping(value = "downloadBase64FromS3")
    public ResponseEntity<Object> downloadBase64FromS3(@RequestHeader("Authorization") String token,
                                                       @RequestParam("file_name") String fileName,
                                                       @RequestParam("user_ref_no") String userRefNo,
                                                       @RequestParam(value = "isNativeFolder", defaultValue = "true", required = false) boolean isNativeFolder,
                                                       @RequestParam(value = "isCustomerPhotos", defaultValue = "false", required = false) boolean isCustomerPhotos) {
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

    @GetMapping(value = "deleteImageOnS3")
    public ResponseEntity<Object> deleteImageOnS3(@RequestHeader("Authorization") String token,
                                                       @RequestParam("file_name") String fileName,
                                                       @RequestParam("user_ref_no") String userRefNo) {
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

    @PostMapping(value = "ocr-check")
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

    @GetMapping(value = "getDocuments")
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

    @PostMapping(value = "send-qr-code-new")
    public ResponseEntity<Object> sendQrCodeNew(@RequestHeader("Authorization") String token,
                                                @RequestParam("paymentReferenceImage") MultipartFile paymentReferenceImage,
                                                @RequestParam("selfieImage") MultipartFile selfieImage,
                                                @RequestParam("data") Object data) throws ConnectorException, JsonProcessingException, InterruptedException {

        DynamicQrCodeResponseDTO result = qrCodeService.sendQrCodeNew(token, data, paymentReferenceImage, selfieImage);
        return new ResponseEntity<>(new BaseDTOResponse<>(result.getData()), HttpStatus.OK);
    }

    @PostMapping(value = "get-qr-code-transaction-status")
    public ResponseEntity<Object> getQrCodeTransactionStatus(@RequestHeader("Authorization") String token, @RequestBody @Valid CommonTransactionStatusCheckRequestDTO reqBody) throws CustomException, ConnectorException, JsonProcessingException, InterruptedException {
        Object result = qrCodeService.getQrCodeTransactionStatus(token, reqBody);
        return new ResponseEntity<>(new BaseDTOResponse<>(result), HttpStatus.OK);
    }

    @PostMapping(value = "qrCodeCallBack")
    public ResponseEntity<Object> qrCodeCallBack(@RequestHeader("Authorization") String token, @RequestBody DynamicQrCodeCallBackRequestDTO reqBody) throws CustomException, InterruptedException {
        Object result = qrCodeService.qrCodeCallBack(token, reqBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "qr-status-check")
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


    @GetMapping(value = "send-otp")
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

    @GetMapping(value = "verify-otp")
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

    @GetMapping(value = "resend-otp")
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

    @GetMapping(value = "get-collaterals")
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


    @GetMapping(value = "mobile-number-validation")
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

    @GetMapping("get-bank-account-details")
    @TimedAlert(threshold = 4000)
    public ResponseEntity<Object> getBankAccountDetails(@RequestHeader("Authorization") String token, @RequestParam("bank_account_id") Long bankAccountId) {
        Object response;
        HttpStatus status;
        try {
            response = utilityService.getBankAccountDetails(bankAccountId);
            status = HttpStatus.OK;
        } catch (Exception e) {
            response = ErrorCode.getErrorCode(Integer.valueOf(e.getMessage()));
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(new BaseDTOResponse<>(response), status);
    }


    @GetMapping(value = "check-transaction-reference-number")
    public ResponseEntity<Object> checkTransactionReferenceNumber(@RequestHeader("Authorization") String token, @RequestParam("transactionReferenceNumber") String transactionReferenceNumber) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            baseResponse = utilityService.checkTransactionReferenceNumber(token, transactionReferenceNumber);
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

    @PostMapping("send-payment-link")
    public ResponseEntity<Object> sendPaymentLink(@RequestHeader("Authorization") String token,
                                                  @RequestParam("paymentReferenceImage") MultipartFile paymentReferenceImage,
                                                  @RequestParam("selfieImage") MultipartFile selfieImage,
                                                  @RequestParam("data") Object data) throws ConnectorException, JsonProcessingException, InterruptedException, ExecutionException {

        Object result = paymentLinkService.sendPaymentLink(token, data, paymentReferenceImage, selfieImage);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "getCollectionIncentiveData")
    public ResponseEntity<Object> getCollectionIncentiveData(@RequestHeader("Authorization") String bearerToken, @RequestBody CollectionIncentiveRequestDTOs collectionIncentiveRequestDTOs) {
        BaseDTOResponse<Object> baseResponse;
        Object dateResponse;
        ResponseEntity<Object> response = null;

        try {
            dateResponse = utilityService.getCollectionIncentiveData(bearerToken, collectionIncentiveRequestDTOs);
            response = new ResponseEntity<>(dateResponse, HttpStatus.OK);
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

    @PostMapping("/emitra-redirect")
    public ResponseEntity<Void> fromEmitraToSynoRedirection(@RequestParam("encData") String encData,
                                                 @RequestParam("logId") String logId,
                                                 @RequestParam("agCode") String agCode,
                                                 @RequestParam("agKey") String agKey) throws Exception {

        DatabaseContextHolder.set("finova");
        String apiKeySecretForEmitra = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(E_MITRA_STATIC_TOKEN);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> apiKeySecretObject = objectMapper.readValue(apiKeySecretForEmitra, new TypeReference<Map<String, Object>>() {});
        String token = utilityService.getTokenByApiKeySecret(apiKeySecretObject);

        //We are using this API only for Finova's e-mitra

        log.info("encrypted data emitra {}", encData);
        log.info("log id emitra {}", logId);
        log.info("ag code emitra {}", agCode);
        log.info("ag key emitra {}", agKey);

        String synoUrl = "https://collections-" + springProfile + ".synofin.tech/emitra?encryptedData=" + encData + "&access_token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, synoUrl);
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }


}
