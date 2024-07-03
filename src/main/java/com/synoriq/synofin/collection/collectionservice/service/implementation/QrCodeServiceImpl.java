package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.QR_CODE_EXPIRATION_CONF;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;

@Slf4j
@Service
public class QrCodeServiceImpl implements QrCodeService, DigitalTransactionChecker {


    public QrCodeServiceImpl(CollectionActivityLogsRepository collectionActivityLogsRepository, DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository,
                             ConsumedApiLogService consumedApiLogService, UtilityService utilityService, ReceiptService receiptService, IntegrationConnectorService integrationConnectorService,
                             CollectionLimitUserWiseRepository collectionLimitUserWiseRepository, CollectionConfigurationsRepository collectionConfigurationsRepository) {
        this.utilityService = utilityService;
        this.consumedApiLogService = consumedApiLogService;
        this.receiptService = receiptService;
        this.integrationConnectorService = integrationConnectorService;
        this.collectionActivityLogsRepository = collectionActivityLogsRepository;
        this.collectionLimitUserWiseRepository = collectionLimitUserWiseRepository;
        this.digitalPaymentTransactionsRepository = digitalPaymentTransactionsRepository;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
    }

    public final CollectionActivityLogsRepository collectionActivityLogsRepository;
    public final DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    public final ConsumedApiLogService consumedApiLogService;
    public final UtilityService utilityService;
    public final ReceiptService receiptService;
    public final IntegrationConnectorService integrationConnectorService;
    public final CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;
    public final CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public DynamicQrCodeResponseDTO sendQrCodeNew(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws ConnectorException, Exception {
        log.info("Begin QR Generate");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(data));
        DynamicQrCodeRequestDTO requestBody = objectMapper.convertValue(jsonNode, DynamicQrCodeRequestDTO.class);
        // always add 1 minute extra in DB
        int expirationMinutes = Integer.parseInt(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF));
        DynamicQrCodeResponseDTO res;
        DynamicQrCodeDataRequestDTO integrationDataRequestBody = new DynamicQrCodeDataRequestDTO();
        DynamicQrCodeIntegrationDataRequestDTO integrationRequestBody = new DynamicQrCodeIntegrationDataRequestDTO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expirationMinutes);
        String validityTime = simpleDateFormat.format(cal.getTime());

        integrationDataRequestBody.setAmount(String.valueOf(requestBody.getAmount()));
        integrationDataRequestBody.setPayerAccount(requestBody.getPayerAccount());
        integrationDataRequestBody.setPayerIFSC(requestBody.getPayerIFSC());
        integrationDataRequestBody.setFirstName(requestBody.getFirstName());
        integrationDataRequestBody.setLastName(requestBody.getLastName());
//        integrationDataRequestBody.setValidityEndDateTime(validityTime);
        String billNumber;
        String merchantTransId;
        if (requestBody.getVendor().equals(KOTAK_VENDOR)) {
            billNumber = requestBody.getLoanId() + "KOTAK" + System.currentTimeMillis();
            merchantTransId = requestBody.getLoanId() + "KOTAK" + System.currentTimeMillis();
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
            ReceiptServiceDtoRequest receiptServiceDtoRequest = objectMapper.convertValue(requestBody.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            receiptServiceDtoRequest.getRequestData().setAutoApproved(true);
            GeoLocationDTO geoLocationDTO = objectMapper.convertValue(receiptServiceDtoRequest.getActivityData().getGeolocationData(), GeoLocationDTO.class);

            List<MultipartFile> allImages = new LinkedList<>();
            if (paymentReferenceImage.getSize() > 0) {
                allImages.add(paymentReferenceImage);
            }
            if (selfieImage.getSize() > 0) {
                allImages.add(selfieImage);
            }
            log.info("paymentReferenceImage {}", paymentReferenceImage.getSize());
            log.info("selfieImage {}", selfieImage.getSize());
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor = new DelegatingSecurityContextExecutorService(executor, SecurityContextHolder.getContext());
            List<Future<UploadImageOnS3ResponseDTO>> allResults = new LinkedList<>();
            for (MultipartFile image : allImages) {
                allResults.add(executor.submit(() -> integrationConnectorService.uploadImageOnS3(token, image, "create_receipt", geoLocationDTO, receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
            }
            executor.shutdown();
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                throw new Exception("ExecutorService did not terminate in the specified time.");
            }
            // Wait for both image uploads to complete
            Map<String, Object> imageMap = new HashMap<>();
            int i = 1;
            for (Future<UploadImageOnS3ResponseDTO> response : allResults) {
                Map<String, Object> currentMap = UtilityService.getStringObjectMapCopy(response.get());
                for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
                    imageMap.put("url" + i, entry.getValue());
                    i++;
                }
            }
            receiptServiceDtoRequest.getActivityData().setImages(imageMap);

            // Checking UserLimit as it is exceeded or not with this amount
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(requestBody.getUserId(), UPI);

            if (collectionLimitUserWiseEntity != null && collectionLimitUserWiseEntity.getTotalLimitValue() < collectionLimitUserWiseEntity.getUtilizedLimitValue() + Double.parseDouble(requestBody.getAmount()))
                throw new Exception("1017003");

            // Calling Generate QR Code API
            res = HTTPRequestService.<Object, DynamicQrCodeResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_QR_CODE_GENERATE_API)
                    .httpHeaders(createHeaders(token))
                    .body(integrationRequestBody)
                    .typeResponseType(DynamicQrCodeResponseDTO.class)
                    .build().call();

            log.info("qr code response -> {}", res);
            // QR code API successFull Response
            if (res.getResponse().equals(true)) {
                String activityRemarks = "Generated a QR code against loan id " + requestBody.getLoanId() + " of payment Rs. " + requestBody.getAmount();
                CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity("generated_dynamic_qr_code", requestBody.getUserId(), requestBody.getLoanId(), activityRemarks, requestBody.getGeolocation(), receiptServiceDtoRequest.getActivityData().getBatteryPercentage());

                collectionActivityLogsRepository.save(collectionActivityLogsEntity);

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
                digitalPaymentTransactionsEntity.setReceiptRequestBody(receiptServiceDtoRequest);
                digitalPaymentTransactionsEntity.setPaymentLink(null);
                digitalPaymentTransactionsEntity.setMobileNo(Long.parseLong(requestBody.getMobileNumber()));
                digitalPaymentTransactionsEntity.setVendor(requestBody.getVendor());
                digitalPaymentTransactionsEntity.setReceiptGenerated(false);
                digitalPaymentTransactionsEntity.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
                digitalPaymentTransactionsEntity.setOtherResponseData(resultNode);

                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
                res.getData().setDigitalPaymentTransactionsId(digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId());
                res.getData().setExpiredTime(validityTime);
            } else {
                throw new ConnectorException(res.getError(), HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
            }
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(), integrationRequestBody, res, "success", requestBody.getLoanId(), HttpMethod.POST.name(), "sendQrCode");
        } catch (ConnectorException ee) {
            String modifiedErrorMessage = utilityService.convertToJSON("Message : " + ee.getMessage() +" RequestId : " +  ee.getRequestId() + "Code : " + ee.getCode());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(), integrationRequestBody, modifiedErrorMessage, "failure", requestBody.getLoanId(), HttpMethod.POST.name(), "sendQrCode");
            throw new ConnectorException(ErrorCode.S3_UPLOAD_DATA_ERROR, ee.getText(), HttpStatus.FAILED_DEPENDENCY, ee.getRequestId());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(), integrationRequestBody, modifiedErrorMessage, "failure", requestBody.getLoanId(), HttpMethod.POST.name(), "sendQrCode");
            log.error("{}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        log.info("Ending QR Generate");
        return res;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public Object getQrCodeTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws Exception {
        log.info("Begin QR Transaction Status");
        DynamicQrCodeCheckStatusResponseDTO res = new DynamicQrCodeCheckStatusResponseDTO();
        Map<String, Object> response = new HashMap<>();
        DynamicQrCodeStatusCheckIntegrationRequestDTO dynamicQrCodeStatusCheckIntegrationRequestDTO = new DynamicQrCodeStatusCheckIntegrationRequestDTO();
        DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO = new DynamicQrCodeStatusCheckDataRequestDTO();
        DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntityData = new DigitalPaymentTransactionsEntity();
        try {
            digitalPaymentTransactionsEntityData = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());
            int expiration = Integer.parseInt(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF));
            if (utilityService.isExpired(expiration, digitalPaymentTransactionsEntityData.getCreatedDate())) {
                digitalPaymentTransactionsEntityData.setStatus("expired");
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntityData);
                return settingResponseData(res);
            }

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
            if (res.getData() == null)
                throw new Exception("1016057");
            String activityRemarks = "The payment status for transaction id " + requestBody.getDigitalPaymentTransactionId() + " and loan id " + digitalPaymentTransactionsEntityData.getLoanId() + " has been updated as " + res.getData().getStatus().toLowerCase() + " by checking the status manually";
            String activityName = "dynamic_qr_code_payment_" + res.getData().getStatus().toLowerCase();
            CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity(activityName, digitalPaymentTransactionsEntityData.getCreatedBy(), digitalPaymentTransactionsEntityData.getLoanId(), activityRemarks, requestBody.getGeolocation(), requestBody.getBatteryPercentage());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            if (res.getData().getStatus().equalsIgnoreCase(QR_CALLBACK_SUCCESS)) {
                log.info("In ifff for success match {}", res);
                if (!digitalPaymentTransactionsEntityData.getReceiptGenerated()) {
                    log.info("receipt generate check {}", res);
                    utilityService.createReceiptByCallBack(digitalPaymentTransactionsEntityData, token, response, res.getData().getOriginalBankRRN());
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
            res.getData().setStatus(res.getData().getStatus().toLowerCase());
            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, dynamicQrCodeStatusCheckIntegrationRequestDTO, res, "success", digitalPaymentTransactionsEntityData.getLoanId(), HttpMethod.POST.name(), "qrCodeTransactionStatus");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, dynamicQrCodeStatusCheckIntegrationRequestDTO, res + modifiedErrorMessage, "failure", digitalPaymentTransactionsEntityData.getLoanId(), HttpMethod.POST.name(), "qrCodeTransactionStatus");
            log.error("QR Transaction Status Exception {}", ee.getMessage());
        }
        log.info("Ending QR Transaction Status");
        return res.getData();
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
                    // calling create receipt function for call back
                    utilityService.createReceiptByCallBack(digitalPaymentTransactionsEntity, token, mainResponse, requestBody.getOriginalBankRRN());
                }
                loanId = digitalPaymentTransactionsEntity.getLoanId();
                if((boolean) mainResponse.get(RECEIPT_GENERATED)) {
                    digitalPaymentTransactionsEntity.setStatus(SUCCESS);
                    digitalPaymentTransactionsEntity.setUtrNumber(requestBody.getOriginalBankRRN());
                    digitalPaymentTransactionsEntity.setCallBackRequestBody(requestBody);
                    digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);

                    log.info("digitalPaymentTransactionsEntity {}", digitalPaymentTransactionsEntity);
                    String activityRemarks = "The payment status for transaction id " + digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId() + " and loan id " + loanId + " has been updated as success";
                    String activityName = "dynamic_qr_code_payment_" + requestBody.getStatus().toLowerCase();
                    CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity(activityName, digitalPaymentTransactionsEntity.getCreatedBy(), loanId, activityRemarks, "{}", 90L);

                    collectionActivityLogsRepository.save(collectionActivityLogsEntity);
                }
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, mainResponse, SUCCESS, loanId, HttpMethod.POST.name(), "qr_callback");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("callback Exception errorMessage {}", errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, errorMessage, FAILURE, loanId, HttpMethod.POST.name(), "qr_callback");
            throw new Exception();
        }
        log.info("Ending QR callback");
        return connectorResponse;
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

    public static HttpHeaders createHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, token);
        httpHeaders.add(CONTENTTYPE, "application/json");
        return httpHeaders;
    }

    @Override
    public Object digitalTransactionStatusCheck(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws Exception {
        return this.getQrCodeTransactionStatus(token, requestBody);
    }

    private DynamicQrCodeCheckStatusResponseDTO settingResponseData(DynamicQrCodeCheckStatusResponseDTO res) {
        DynamicQrCodeCheckStatusDataResponseDTO dynamicQrCodeCheckStatusDataResponseDTO = DynamicQrCodeCheckStatusDataResponseDTO.builder()
                .status("expired")
                .build();
        res.setResponse(true);
        res.setData(dynamicQrCodeCheckStatusDataResponseDTO);
        res.setRequestId("");
        return res;
    }
}
