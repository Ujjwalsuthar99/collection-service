package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.QrCodeService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;
import static com.synoriq.synofin.collection.collectionservice.common.QRCodeVariables.*;

@Slf4j
@Service
public class QrCodeServiceImpl implements QrCodeService {


    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;

    @Autowired
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    UtilityService utilityService;

    @Autowired
    ReceiptService receiptService;

    @Autowired
    CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;


    @Override
    @Transactional(rollbackOn = Exception.class)
    public DynamicQrCodeResponseDTO sendQrCode(String token, DynamicQrCodeRequestDTO requestBody) throws Exception {
        log.info("Begin QR Generate");
        DynamicQrCodeResponseDTO res = null;
        DynamicQrCodeDataRequestDTO integrationDataRequestBody = new DynamicQrCodeDataRequestDTO();
        DynamicQrCodeIntegrationDataRequestDTO integrationRequestBody = new DynamicQrCodeIntegrationDataRequestDTO();

        integrationDataRequestBody.setAmount(String.valueOf(requestBody.getAmount()));
        integrationDataRequestBody.setPayerAccount(requestBody.getPayerAccount());
        integrationDataRequestBody.setPayerIFSC(requestBody.getPayerIFSC());
        integrationDataRequestBody.setFirstName(requestBody.getFirstName());
        integrationDataRequestBody.setLastName(requestBody.getLastName());
        String billNumber;
        String merchantTransId;
        if (requestBody.getVendor().equals(KOTAK_VENDOR)) {
            billNumber = "." + requestBody.getLoanId() + "." + System.currentTimeMillis();
            merchantTransId = "." + requestBody.getLoanId() + "." + System.currentTimeMillis();
        } else {
            billNumber = requestBody.getLoanId() + "_" + System.currentTimeMillis();
            merchantTransId = requestBody.getLoanId() + "_" + System.currentTimeMillis();
        }
        integrationDataRequestBody.setBillNumber(billNumber);
        integrationDataRequestBody.setMerchantTranId(merchantTransId);

        integrationRequestBody.setDynamicQrCodeDataRequestDTO(integrationDataRequestBody);
        integrationRequestBody.setSystemId(COLLECTION);
        integrationRequestBody.setUserReferenceNumber(String.valueOf(requestBody.getUserId()));
        integrationRequestBody.setSpecificPartnerName(requestBody.getVendor());

        try {
            // Checking UserLimit as it is exceeded or not with this amount
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(requestBody.getUserId(), UPI);
            if(collectionLimitUserWiseEntity != null) {
                if (collectionLimitUserWiseEntity.getTotalLimitValue() < collectionLimitUserWiseEntity.getUtilizedLimitValue() + Double.parseDouble(requestBody.getAmount()))
                    throw new Exception("1016053");
            }

            // Calling Generate QR Code API
            res = HTTPRequestService.<Object, DynamicQrCodeResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_QR_CODE_GENERATE_API)
                    .httpHeaders(createHeaders(token))
                    .body(integrationRequestBody)
                    .typeResponseType(DynamicQrCodeResponseDTO.class)
                    .build().call();

            if (res.getData() == null) {
                log.info("QR Null Response {}", res);
                throw new Exception("1016052");
            }
            DynamicQrCodeDataResponseDTO dynamicQrCodeDataResponseDTO = new DynamicQrCodeDataResponseDTO();
            dynamicQrCodeDataResponseDTO.setMerchantTranId(merchantTransId);
            dynamicQrCodeDataResponseDTO.setLink(res.getData().getLink());
            dynamicQrCodeDataResponseDTO.setStatus(res.getData().getStatus());

            DynamicQrCodeResponseDTO dynamicQrCodeResponseDto = new DynamicQrCodeResponseDTO();
            dynamicQrCodeResponseDto.setResponse(res.getResponse());
            dynamicQrCodeResponseDto.setRequestId(res.getRequestId());
            dynamicQrCodeResponseDto.setData(dynamicQrCodeDataResponseDTO);
            res = dynamicQrCodeResponseDto;

            // QR code API successFull Response
            if (res.getResponse().equals(true)) {
                String activityRemarks = "Generated a QR code against loan id " + requestBody.getLoanId() + " of payment Rs. " + requestBody.getAmount();
                CollectionActivityLogsEntity collectionActivityLogsEntity = getCollectionActivityLogsEntity("generated_dynamic_qr_code", requestBody.getUserId(), requestBody.getLoanId(), activityRemarks, requestBody.getGeolocation());

                collectionActivityLogsRepository.save(collectionActivityLogsEntity);

                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode resultNode = objectMapper.createObjectNode();

                ObjectNode requestNode = objectMapper.valueToTree(integrationDataRequestBody);
                ObjectNode responseNode = objectMapper.valueToTree(res);
                resultNode.set("request", requestNode);
                resultNode.set("response", responseNode);


                DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = new DigitalPaymentTransactionsEntity();
                digitalPaymentTransactionsEntity.setCreatedDate(new Date());
                digitalPaymentTransactionsEntity.setCreatedBy(requestBody.getUserId());
                digitalPaymentTransactionsEntity.setModifiedDate(null);
                digitalPaymentTransactionsEntity.setModifiedBy(null);
                digitalPaymentTransactionsEntity.setLoanId(requestBody.getLoanId());
                digitalPaymentTransactionsEntity.setPaymentServiceName(DYNAMIC_QR_CODE);
                digitalPaymentTransactionsEntity.setStatus(PENDING);
                digitalPaymentTransactionsEntity.setMerchantTranId(merchantTransId);
                digitalPaymentTransactionsEntity.setAmount(Float.parseFloat(requestBody.getAmount()));
                digitalPaymentTransactionsEntity.setUtrNumber(null);
                digitalPaymentTransactionsEntity.setReceiptRequestBody(requestBody.getReceiptRequestBody());
                digitalPaymentTransactionsEntity.setPaymentLink(null);
                digitalPaymentTransactionsEntity.setMobileNo(Long.parseLong(requestBody.getMobileNumber()));
                digitalPaymentTransactionsEntity.setVendor(requestBody.getVendor());
                digitalPaymentTransactionsEntity.setReceiptGenerated(false);
                digitalPaymentTransactionsEntity.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
                digitalPaymentTransactionsEntity.setOtherResponseData(resultNode);

                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
                dynamicQrCodeDataResponseDTO.setDigitalPaymentTransactionsId(digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId());
            }

            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(), integrationRequestBody, res, "success", requestBody.getLoanId());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage() + res;
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(), integrationRequestBody, modifiedErrorMessage + res, "failure", requestBody.getLoanId());
            log.error("{}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        log.info("Ending QR Generate");
        return res;
    }



    @Override
    @Transactional(rollbackOn = Exception.class)
    public DynamicQrCodeCheckStatusResponseDTO getQrCodeTransactionStatus(String token, DynamicQrCodeStatusCheckRequestDTO requestBody) throws Exception {
        log.info("Begin QR Transaction Status");
        DynamicQrCodeCheckStatusResponseDTO res = new DynamicQrCodeCheckStatusResponseDTO();
        Map<String, Object> response = new HashMap<>();
        DynamicQrCodeStatusCheckIntegrationRequestDTO dynamicQrCodeStatusCheckIntegrationRequestDTO = new DynamicQrCodeStatusCheckIntegrationRequestDTO();
        DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO = new DynamicQrCodeStatusCheckDataRequestDTO();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntityData = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());
            dynamicQrCodeStatusCheckDataRequestDTO.setMerchantTranId(requestBody.getMerchantTranId());
            dynamicQrCodeStatusCheckDataRequestDTO.setCustomerId("91" + digitalPaymentTransactionsEntityData.getMobileNo().toString());

            // Creating Transaction Status DTO
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setDynamicQrCodeStatusCheckDataRequestDTO(dynamicQrCodeStatusCheckDataRequestDTO);
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setUserReferenceNumber(String.valueOf(digitalPaymentTransactionsEntityData.getCreatedBy()));
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setSystemId(COLLECTION);
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setSpecificPartnerName(digitalPaymentTransactionsEntityData.getVendor());

            // Calling Transaction Status Check API
            res = HTTPRequestService.<Object, DynamicQrCodeCheckStatusResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_QR_CODE_TRANSACTION_STATUS_API)
                    .httpHeaders(createHeaders(token))
                    .body(dynamicQrCodeStatusCheckIntegrationRequestDTO)
                    .typeResponseType(DynamicQrCodeCheckStatusResponseDTO.class)
                    .build().call();

            log.info("response from qr status check {}", res);

            String activityRemarks = "The payment status for transaction id " + requestBody.getDigitalPaymentTransactionId() + " and loan id " + digitalPaymentTransactionsEntityData.getLoanId() + " has been updated as " + res.getData().getStatus().toLowerCase() + " by checking the status manually";
            String activityName = "dynamic_qr_code_payment_" + res.getData().getStatus().toLowerCase();
            CollectionActivityLogsEntity collectionActivityLogsEntity = getCollectionActivityLogsEntity(activityName, digitalPaymentTransactionsEntityData.getCreatedBy(), digitalPaymentTransactionsEntityData.getLoanId(), activityRemarks, requestBody.getGeolocation());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            if (res.getData().getStatus().equalsIgnoreCase(QR_CALLBACK_SUCCESS)) {
                log.info("In ifff for success match {}", res);
                if (!digitalPaymentTransactionsEntityData.getReceiptGenerated()) {
                    log.info("receipt generate check {}", res);
                    createReceiptByCallBack(digitalPaymentTransactionsEntityData, token, response, res.getData().getOriginalBankRRN());
                    log.info("create receipt done");
                } else {
                    Map<String, Object> respMap = new ObjectMapper().convertValue(digitalPaymentTransactionsEntityData.getReceiptResponse(), Map.class);
                    response.put(STATUS, res.getData().getStatus().toLowerCase());
                    response.put(RECEIPT_GENERATED, true);
                    response.put(SR_ID, String.valueOf(respMap.get("service_request_id")));
                }
            } else {
                response.put(STATUS, FAILURE);
                response.put(RECEIPT_GENERATED, digitalPaymentTransactionsEntityData.getReceiptGenerated());
                response.put(SR_ID, null);
            }

            digitalPaymentTransactionsEntityData.setUtrNumber(res.getData().getOriginalBankRRN());
            digitalPaymentTransactionsEntityData.setStatus(res.getData().getStatus().toLowerCase());
            digitalPaymentTransactionsEntityData.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());

            digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntityData);

            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, dynamicQrCodeStatusCheckIntegrationRequestDTO, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, dynamicQrCodeStatusCheckIntegrationRequestDTO, modifiedErrorMessage, "failure", null);
            log.error("QR Transaction Status Exception {}", ee.getMessage());
        }
        log.info("Ending QR Transaction Status");
        return res;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws Exception {
        log.info("Begin QR callback");
        String merchantTransId = requestBody.getMerchantTranId();
        Map<String, Object> mainResponse = new HashMap<>();
        Map<String, Object> connectorResponse = new HashMap<>();
        Long loanId = null;

        try {
            log.info("hurray! callback received for QR");
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByMerchantTranId(merchantTransId);
            if (digitalPaymentTransactionsEntity != null) {
                if (Objects.equals(requestBody.getStatus(), QR_CALLBACK_SUCCESS) && !digitalPaymentTransactionsEntity.getReceiptGenerated()) {
                    digitalPaymentTransactionsEntity.setStatus(SUCCESS);
                    digitalPaymentTransactionsEntity.setUtrNumber(requestBody.getOriginalBankRRN());
                    // calling create receipt function for call back
                    createReceiptByCallBack(digitalPaymentTransactionsEntity, token, mainResponse, requestBody.getOriginalBankRRN());
                }
                loanId = digitalPaymentTransactionsEntity.getLoanId();
                digitalPaymentTransactionsEntity.setCallBackRequestBody(requestBody);
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);


                String activityRemarks = "The payment status for transaction id " + digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId() + " and loan id " + loanId + " has been updated as success";
                String activityName = "dynamic_qr_code_payment_" + requestBody.getStatus().toLowerCase();
                CollectionActivityLogsEntity collectionActivityLogsEntity = getCollectionActivityLogsEntity(activityName, digitalPaymentTransactionsEntity.getCreatedBy(), loanId, activityRemarks, "{}");

                collectionActivityLogsRepository.save(collectionActivityLogsEntity);
                connectorResponse.put(STATUS, true);
                mainResponse.put(STATUS, requestBody.getStatus().toLowerCase());
                mainResponse.put(CONNECTOR_RESPONSE, connectorResponse);
            } else {
                connectorResponse.put(STATUS, false);
                mainResponse.put(STATUS, null);
                mainResponse.put(RECEIPT_GENERATED, null);
                mainResponse.put(SR_ID, null);
                mainResponse.put(CONNECTOR_RESPONSE, connectorResponse);

            }
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, mainResponse, SUCCESS, loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("callback Exception errorMessage {}", errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, errorMessage, FAILURE, loanId);
            throw new Exception();
        }
        log.info("Ending QR callback");
        return connectorResponse;
    }

    private void createReceiptByCallBack(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity, String token, Map<String, Object> response, String utrNumber) throws Exception {
        log.info("Begin callback create receipt function");
        try {
            CurrentUserInfo currentUserInfo = new CurrentUserInfo();
            // implementing create receipt here
            ReceiptServiceDtoRequest receiptServiceDtoRequest = new ObjectMapper().convertValue(digitalPaymentTransactionsEntity.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            receiptServiceDtoRequest.getRequestData().getRequestData().setTransactionReference(utrNumber);
            ServiceRequestSaveResponse resp = receiptService.createReceipt(receiptServiceDtoRequest, token, true);
            log.info("receipt response {}", resp);
            digitalPaymentTransactionsEntity.setReceiptResponse(resp.getData());
            if (resp.getData() != null && resp.getData().getServiceRequestId() != null) {
                log.info("in ifff receipt response {}", resp);
                response.put(RECEIPT_GENERATED, true);
                response.put(SR_ID, resp.getData().getServiceRequestId());
                digitalPaymentTransactionsEntity.setReceiptGenerated(true);
                String url = GET_PDF_API + resp.getData().getServiceRequestId();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBearerAuth(token);

                ResponseEntity<byte[]> res;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
                res = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(httpHeaders),
                        byte[].class);

//                String encodedString = Base64.getEncoder().encodeToString(res.getBody());

                byte[] byteArray = res.getBody();
                String filename = "file.jpg";

                DiskFileItem fileItem = new DiskFileItem("file", "application/pdf", true, filename, byteArray.length, new java.io.File(System.getProperty("java.io.tmpdir")));
                fileItem.getOutputStream().write(byteArray);

                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                String updatedFileName = receiptServiceDtoRequest.getRequestData().getLoanId() + "_" + new Date().getTime() + "_receipt_image.pdf";
                String userRef = "receipt/" + receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy();
                // hitting send sms to customer
                utilityService.sendPdfToCustomerUsingS3(token, multipartFile, userRef, currentUserInfo.getClientId(), receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode(), receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount(), updatedFileName,
                        receiptServiceDtoRequest.getActivityData().getUserId().toString(), receiptServiceDtoRequest.getCustomerType(),
                        receiptServiceDtoRequest.getCustomerName(), receiptServiceDtoRequest.getApplicantMobileNumber(), receiptServiceDtoRequest.getCollectedFromNumber(), receiptServiceDtoRequest.getLoanApplicationNumber(), resp.getData().getServiceRequestId());
                log.info("in callback create receipt function ending");
            }
        } catch (Exception e) {
            log.error("Error while create receipt via callback {}", e.getMessage());
            throw new Exception(e.getMessage());
        }
        log.info("Ending callback create receipt function");
    }


    @Override
    public Object qrStatusCheck(String token, String merchantId) throws Exception {
        Map<String, Object> resp = new HashMap<>();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByMerchantTranId(merchantId);
            if (digitalPaymentTransactionsEntity != null) {
                resp.put(STATUS, digitalPaymentTransactionsEntity.getStatus());
            } else {
                throw new Exception("");
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return new BaseDTOResponse<>(resp);
    }


    @NotNull
    private static CollectionActivityLogsEntity getCollectionActivityLogsEntity(String activityName, Long userId, Long loanId, String remarks, Object geoLocation) {
        CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
        collectionActivityLogsEntity.setActivityName(activityName);
        collectionActivityLogsEntity.setActivityDate(new Date());
        collectionActivityLogsEntity.setDeleted(false);
        collectionActivityLogsEntity.setActivityBy(userId);
        collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
        collectionActivityLogsEntity.setAddress("{}");
        collectionActivityLogsEntity.setRemarks(remarks);
        collectionActivityLogsEntity.setImages("{}");
        collectionActivityLogsEntity.setLoanId(loanId);
        collectionActivityLogsEntity.setGeolocation(geoLocation);
        return collectionActivityLogsEntity;
    }

    public static HttpHeaders createHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, token);
        httpHeaders.add(CONTENTTYPE, "application/json");
        return httpHeaders;
    }

}
