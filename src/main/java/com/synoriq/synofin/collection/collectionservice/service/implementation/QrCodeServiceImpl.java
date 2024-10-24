package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos.DynamicQrCodeCheckStatusDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;

@Slf4j
@Service
public class QrCodeServiceImpl implements QrCodeService, DigitalTransactionChecker {

    private static final String STATUS_MESSAGE = "success";
    private static final String QR_CODE_STATUS = "qrCodeTransactionStatus";
    private static final String SEND_QR_STATUS = "sendQrCode";
    private static final String FAILURE_STATUE = "failure";
    private static final String QR_CALLBACK_STATUS = "qr_callback";
    private static final String INTERRUPTED_EXCEPTION_STR = "Interrupted Exception Error {}";

    public QrCodeServiceImpl(CollectionActivityLogsRepository collectionActivityLogsRepository,
            DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository,
            ConsumedApiLogService consumedApiLogService, UtilityService utilityService, ReceiptService receiptService,
            IntegrationConnectorService integrationConnectorService,
            CollectionLimitUserWiseRepository collectionLimitUserWiseRepository,
            CollectionConfigurationsRepository collectionConfigurationsRepository, RestTemplate restTemplate) {
        this.utilityService = utilityService;
        this.consumedApiLogService = consumedApiLogService;
        this.receiptService = receiptService;
        this.integrationConnectorService = integrationConnectorService;
        this.collectionActivityLogsRepository = collectionActivityLogsRepository;
        this.collectionLimitUserWiseRepository = collectionLimitUserWiseRepository;
        this.digitalPaymentTransactionsRepository = digitalPaymentTransactionsRepository;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
        this.restTemplate = restTemplate;
    }

    public final CollectionActivityLogsRepository collectionActivityLogsRepository;
    public final DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    public final ConsumedApiLogService consumedApiLogService;
    public final UtilityService utilityService;
    public final ReceiptService receiptService;
    public final RestTemplate restTemplate;
    public final IntegrationConnectorService integrationConnectorService;
    public final CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;
    public final CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public DynamicQrCodeResponseDTO sendQrCodeNew(String token, Object data, MultipartFile paymentReferenceImage,
            MultipartFile selfieImage) throws ConnectorException, JsonProcessingException, InterruptedException {
        log.info("Begin QR Generate");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(data));
        DynamicQrCodeRequestDTO requestBody = objectMapper.convertValue(jsonNode, DynamicQrCodeRequestDTO.class);

        String qrExpiration = collectionConfigurationsRepository
                .findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF);

        // always add 1 minute extra in DB
        int expirationMinutes = qrExpiration.equals("dayend") ? findDayEndRemainingMinute()
                : Integer.parseInt(collectionConfigurationsRepository
                        .findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF));
        DynamicQrCodeResponseDTO res;
        DynamicQrCodeDataRequestDTO integrationDataRequestBody = new DynamicQrCodeDataRequestDTO();
        DynamicQrCodeIntegrationDataRequestDTO integrationRequestBody = new DynamicQrCodeIntegrationDataRequestDTO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expirationMinutes);
        String validityTime = expirationMinutes == 0 ? null : simpleDateFormat.format(cal.getTime());

        integrationDataRequestBody.setAmount(String.valueOf(requestBody.getAmount()));
        integrationDataRequestBody.setPayerAccount(requestBody.getPayerAccount());
        integrationDataRequestBody.setPayerIFSC(requestBody.getPayerIFSC());
        integrationDataRequestBody.setFirstName(requestBody.getFirstName());
        integrationDataRequestBody.setLastName(requestBody.getLastName());
        integrationDataRequestBody.setValidityEndDateTime(validityTime);

        String billNumber;
        String merchantTransId;
        if (requestBody.getVendor().equals(KOTAK_VENDOR)) {
            billNumber = requestBody.getLoanId() + "KOTAK" + System.currentTimeMillis();
            merchantTransId = requestBody.getLoanId() + "KOTAK" + System.currentTimeMillis();
        } else {
            billNumber = requestBody.getLoanId() + "ICICI" + System.currentTimeMillis();
            merchantTransId = requestBody.getLoanId() + "ICICI" + System.currentTimeMillis();
        }
        integrationDataRequestBody.setBillNumber(billNumber);
        integrationDataRequestBody.setMerchantTranId(merchantTransId);

        integrationRequestBody.setDynamicQrCodeDataRequestDTO(integrationDataRequestBody);
        integrationRequestBody.setSystemId(COLLECTION);
        integrationRequestBody.setUserReferenceNumber(String.valueOf(requestBody.getUserId()));
        integrationRequestBody.setSpecificPartnerName(requestBody.getVendor());

        long validationTime = Long.parseLong(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RECEIPT_TIME_VALIDATE));
        try {

            DigitalPaymentTransactionsEntity createReceiptTimeError = digitalPaymentTransactionsRepository.findFirstByLoanIdAndAmount(requestBody.getLoanId(), Float.valueOf(requestBody.getAmount()));
            if (createReceiptTimeError != null) {
                String dateTime = String.valueOf(createReceiptTimeError.getCreatedDate()); // 2023-05-18 18:23:30.292
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = dateFormat.parse(dateTime);
                Date currentDateTime = new Date();
                long timeDifference = (currentDateTime.getTime() - newDate.getTime()) / (60 * 1000);
                if (timeDifference < validationTime) {
                    ErrorCode errCode = ErrorCode.getErrorCode(1016038);
                    throw new CollectionException(errCode, 1016038);
                }
            }

            // Checking UserLimit as it is exceeded or not with this amount
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository
                    .getCollectionLimitUserWiseByUserId(requestBody.getUserId(), UPI);

            if (collectionLimitUserWiseEntity != null && collectionLimitUserWiseEntity
                    .getTotalLimitValue() < collectionLimitUserWiseEntity.getUtilizedLimitValue()
                            + Double.parseDouble(requestBody.getAmount())) {
                ErrorCode errCode = ErrorCode.getErrorCode(1017003);
                throw new CollectionException(errCode, 1017003);
            }
            String paymentBank = collectionConfigurationsRepository
                    .findConfigurationValueByConfigurationName("static_payment_bank_for_upi");
            ReceiptServiceDtoRequest receiptServiceDtoRequest = objectMapper
                    .convertValue(requestBody.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            receiptServiceDtoRequest.getRequestData().setAutoApproved(true);
            receiptServiceDtoRequest.getRequestData().getRequestData().setPaymentBank(paymentBank.equals("false") ? "" : paymentBank);
            GeoLocationDTO geoLocationDTO = objectMapper.convertValue(
                    receiptServiceDtoRequest.getActivityData().getGeolocationData(), GeoLocationDTO.class);

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
                allResults.add(executor.submit(() -> integrationConnectorService.uploadImageOnS3(token, image,
                        "create_receipt", geoLocationDTO,
                        receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
            }
            executor.shutdown();
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                throw new CustomException("ExecutorService did not terminate in the specified time.");
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

            // Calling Generate QR Code API
            res = HTTPRequestService.<Object, DynamicQrCodeResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_QR_CODE_GENERATE_API)
                    .httpHeaders(createHeaders(token))
                    .body(integrationRequestBody)
                    .typeResponseType(DynamicQrCodeResponseDTO.class)
                    .build().call(restTemplate);

            log.info("qr code response -> {}", res);
            // QR code API successFull Response
            if (res.getResponse().equals(true) && res.getData().getStatus().equals("true")) {
                String activityRemarks = "Generated a QR code against loan id " + requestBody.getLoanId()
                        + " of payment Rs. " + requestBody.getAmount();
                CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService
                        .getCollectionActivityLogsEntity("generated_dynamic_qr_code", requestBody.getUserId(),
                                requestBody.getLoanId(), activityRemarks, requestBody.getGeolocation(),
                                receiptServiceDtoRequest.getActivityData().getBatteryPercentage());

                collectionActivityLogsRepository.save(collectionActivityLogsEntity);
                res.getData().setExpiredTime(validityTime);
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
                digitalPaymentTransactionsEntity
                        .setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
                digitalPaymentTransactionsEntity.setOtherResponseData(resultNode);
                log.info("digitalPaymentTransactionsEntity -> {}", digitalPaymentTransactionsEntity);
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
                res.getData().setDigitalPaymentTransactionsId(
                        digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId());
                res.getData().setMerchantTranId(merchantTransId);
                res.getData().setCreatedDate(new Date());
            } else {
                throw new ConnectorException(res.getError(), HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
            }
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(),
                    integrationRequestBody, res, STATUS_MESSAGE, requestBody.getLoanId(), HttpMethod.POST.name(),
                    SEND_QR_STATUS);
        } catch (ConnectorException ee) {
            String modifiedErrorMessage = utilityService.convertToJSON(
                    "Message : " + ee.getMessage() + " RequestId : " + ee.getRequestId() + "Code : " + ee.getCode());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(),
                    integrationRequestBody, modifiedErrorMessage, FAILURE_STATUE, requestBody.getLoanId(),
                    HttpMethod.POST.name(), SEND_QR_STATUS);
            throw new ConnectorException(ErrorCode.S3_UPLOAD_DATA_ERROR, ee.getText(), HttpStatus.FAILED_DEPENDENCY,
                    ee.getRequestId());
        } catch (InterruptedException ee) {
            log.error(INTERRUPTED_EXCEPTION_STR, ee.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ee.getMessage());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, requestBody.getUserId(),
                    integrationRequestBody, modifiedErrorMessage, FAILURE_STATUE, requestBody.getLoanId(),
                    HttpMethod.POST.name(), SEND_QR_STATUS);
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }
        log.info("Ending QR Generate");
        return res;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Object getQrCodeTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody)
            throws CustomException, ConnectorException, JsonProcessingException, InterruptedException {
        log.info("Begin QR Transaction Status");
        DynamicQrCodeCheckStatusResponseDTO res = new DynamicQrCodeCheckStatusResponseDTO();
        Map<String, Object> response = new HashMap<>();
        Long loanId = null;
        DynamicQrCodeStatusCheckIntegrationRequestDTO dynamicQrCodeStatusCheckIntegrationRequestDTO = new DynamicQrCodeStatusCheckIntegrationRequestDTO();
        DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO = new DynamicQrCodeStatusCheckDataRequestDTO();
        DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntityData = new DigitalPaymentTransactionsEntity();
        try {
            digitalPaymentTransactionsEntityData = digitalPaymentTransactionsRepository
                    .findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());
            loanId = digitalPaymentTransactionsEntityData.getLoanId();
            dynamicQrCodeStatusCheckDataRequestDTO.setMerchantTranId(requestBody.getMerchantTranId());
            dynamicQrCodeStatusCheckDataRequestDTO
                    .setCustomerId("91" + digitalPaymentTransactionsEntityData.getMobileNo().toString());

            // Adding Validation here //
            ConsumedApiLogsEntity consumedApiLogsEntity = consumedApiLogService.getLastDataByLoanIdAndLogName(loanId,
                    EnumSQLConstants.LogNames.check_qr_payment_status);
            if (consumedApiLogsEntity != null
                    && utilityService.isExpired(10, consumedApiLogsEntity.getCreatedDate(), false)) {
                ErrorCode errorCode = ErrorCode.getErrorCode(1016058, "Check status will be available at "
                        + utilityService.addMinutes(10, consumedApiLogsEntity.getCreatedDate()));
                throw new CustomException(errorCode);
            }

            // Creating Transaction Status DTO
            dynamicQrCodeStatusCheckIntegrationRequestDTO
                    .setDynamicQrCodeStatusCheckDataRequestDTO(dynamicQrCodeStatusCheckDataRequestDTO);
            dynamicQrCodeStatusCheckIntegrationRequestDTO
                    .setUserReferenceNumber(String.valueOf(digitalPaymentTransactionsEntityData.getCreatedBy()));
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setSystemId(COLLECTION);
            dynamicQrCodeStatusCheckIntegrationRequestDTO
                    .setSpecificPartnerName(digitalPaymentTransactionsEntityData.getVendor());

            // Calling Transaction Status Check API
            res = HTTPRequestService.<Object, DynamicQrCodeCheckStatusResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_QR_CODE_TRANSACTION_STATUS_API)
                    .httpHeaders(createHeaders(token))
                    .body(dynamicQrCodeStatusCheckIntegrationRequestDTO)
                    .typeResponseType(DynamicQrCodeCheckStatusResponseDTO.class)
                    .build().call(restTemplate);

            log.info("response from qr status check {}", res);
            if (Objects.requireNonNull(res).getData() == null) {
                throw new ConnectorException(res.toString());
            }
            String activityRemarks = "The payment status for transaction id "
                    + requestBody.getDigitalPaymentTransactionId() + " and loan id " + loanId + " has been updated as "
                    + res.getData().getStatus().toLowerCase() + " by checking the status manually";
            String activityName = "dynamic_qr_code_payment_" + res.getData().getStatus().toLowerCase();
            CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity(
                    activityName, digitalPaymentTransactionsEntityData.getCreatedBy(), loanId, activityRemarks,
                    requestBody.getGeolocation(), requestBody.getBatteryPercentage());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            if (res.getData().getStatus().equalsIgnoreCase(QR_CALLBACK_SUCCESS)) {
                log.info("In ifff for success match {}", res);
                if (Boolean.FALSE.equals(digitalPaymentTransactionsEntityData.getReceiptGenerated())) {
                    log.info("receipt generate check {}", res);
                    utilityService.createReceiptByCallBack(digitalPaymentTransactionsEntityData, token, response,
                            res.getData().getOriginalBankRRN());
                    log.info("create receipt done");
                } else {
                    Map<String, Object> respMap = new ObjectMapper()
                            .convertValue(digitalPaymentTransactionsEntityData.getReceiptResponse(), Map.class);
                    response.put(STATUS, res.getData().getStatus().toLowerCase());
                    response.put(RECEIPT_GENERATED, true);
                    response.put(SR_ID, String.valueOf(respMap.get("service_request_id")));
                }
            } else {
                response.put(STATUS, FAILURE);
                response.put(RECEIPT_GENERATED, digitalPaymentTransactionsEntityData.getReceiptGenerated());
                response.put(SR_ID, null);
                String qrExpiration = collectionConfigurationsRepository
                        .findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF);
                int expirationMinutes = qrExpiration.equals("dayend") ? findDayEndRemainingMinute()
                        : Integer.parseInt(collectionConfigurationsRepository
                                .findConfigurationValueByConfigurationName(QR_CODE_EXPIRATION_CONF));
                if (res.getData().getStatus().equalsIgnoreCase(PENDING) && utilityService.isExpired(expirationMinutes,
                        digitalPaymentTransactionsEntityData.getCreatedDate(), true)) {
                    digitalPaymentTransactionsEntityData.setStatus("expired");
                    digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntityData);
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null,
                            dynamicQrCodeStatusCheckIntegrationRequestDTO, settingResponseData(), STATUS_MESSAGE,
                            loanId, HttpMethod.POST.name(), QR_CODE_STATUS);
                    return settingResponseData();
                }
            }

            digitalPaymentTransactionsEntityData.setUtrNumber(res.getData().getOriginalBankRRN());
            digitalPaymentTransactionsEntityData.setStatus(res.getData().getStatus().toLowerCase());
            digitalPaymentTransactionsEntityData
                    .setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());

            digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntityData);
            res.getData().setStatus(res.getData().getStatus().toLowerCase());
            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null,
                    dynamicQrCodeStatusCheckIntegrationRequestDTO, res, STATUS_MESSAGE, loanId, HttpMethod.POST.name(),
                    QR_CODE_STATUS);
        } catch (CustomException ee) {
            throw new CustomException(ee.getMessage(), ee.getCode());
        } catch (ConnectorException ee) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            IntegrationServiceErrorResponseDTO r = new ObjectMapper().readValue(ow.writeValueAsString(res.getError()),
                    IntegrationServiceErrorResponseDTO.class);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status,
                    digitalPaymentTransactionsEntityData.getCreatedBy(), dynamicQrCodeStatusCheckIntegrationRequestDTO,
                    res, FAILURE_STATUE, loanId, HttpMethod.POST.name(), QR_CODE_STATUS);
            throw new ConnectorException(r, HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
        } catch(InterruptedException ie) {
            log.error(INTERRUPTED_EXCEPTION_STR, ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // adding error message in connector response explicitly
            res.setErrorFields(modifiedErrorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null,
                    dynamicQrCodeStatusCheckIntegrationRequestDTO, res, FAILURE_STATUE,
                    digitalPaymentTransactionsEntityData.getLoanId(), HttpMethod.POST.name(), QR_CODE_STATUS);
            log.error("QR Transaction Status Exception {}", ee.getMessage());
        }
        log.info("Ending QR Transaction Status");
        return res.getData();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws CustomException, InterruptedException {
        log.info("Begin QR callback");
        String merchantTransId = requestBody.getMerchantTranId();
        Map<String, Object> mainResponse = new HashMap<>();
        mainResponse.put(STATUS, null);
        mainResponse.put(RECEIPT_GENERATED, false);
        mainResponse.put(SR_ID, null);
        Map<String, Object> connectorResponse = new HashMap<>();
        connectorResponse.put(STATUS, false);
        Long loanId = null;
        log.info("hurray! callback received for QR {}", merchantTransId);
        DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository
                .findByMerchantTranId(merchantTransId);
        try {
            if (digitalPaymentTransactionsEntity != null) {
                if (Objects.equals(requestBody.getStatus(), QR_CALLBACK_SUCCESS)
                        && (!digitalPaymentTransactionsEntity.getReceiptGenerated())) {
                    // calling create receipt function for call back
                    utilityService.createReceiptByCallBack(digitalPaymentTransactionsEntity, token, mainResponse,
                            requestBody.getOriginalBankRRN());
                }
                loanId = digitalPaymentTransactionsEntity.getLoanId();
                if ((boolean) mainResponse.get(RECEIPT_GENERATED)) {
                    digitalPaymentTransactionsEntity.setStatus(SUCCESS);
                    digitalPaymentTransactionsEntity.setUtrNumber(requestBody.getOriginalBankRRN());
                    digitalPaymentTransactionsEntity.setCallBackRequestBody(requestBody);
                    digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);

                    log.info("digitalPaymentTransactionsEntity {}", digitalPaymentTransactionsEntity);
                    String activityRemarks = "The payment status for transaction id "
                            + digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId() + " and loan id "
                            + loanId + " has been updated as success";
                    String activityName = "dynamic_qr_code_payment_" + requestBody.getStatus().toLowerCase();
                    CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService
                            .getCollectionActivityLogsEntity(activityName,
                                    digitalPaymentTransactionsEntity.getCreatedBy(), loanId, activityRemarks, "{}",
                                    90L);

                    collectionActivityLogsRepository.save(collectionActivityLogsEntity);
                }
                connectorResponse.replace(STATUS, true);
                mainResponse.replace(STATUS, requestBody.getStatus().toLowerCase());
                mainResponse.put(CONNECTOR_RESPONSE, connectorResponse);
            } else {
                mainResponse.put(CONNECTOR_RESPONSE, connectorResponse);

            }
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody,
                    utilityService.convertToJSON(mainResponse), SUCCESS, loanId, HttpMethod.POST.name(),
                    QR_CALLBACK_STATUS);
        } catch (CustomException ee) {
            if (digitalPaymentTransactionsEntity != null) {
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
            }
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody,
                    utilityService.convertToJSON(ee.getText()), FAILURE, loanId, HttpMethod.POST.name(),
                    QR_CALLBACK_STATUS);
        } catch(InterruptedException ie) {
            log.error(INTERRUPTED_EXCEPTION_STR, ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("callback Exception errorMessage {}", errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody,
                    utilityService.convertToJSON(errorMessage), FAILURE, loanId, HttpMethod.POST.name(),
                    QR_CALLBACK_STATUS);
            throw new CustomException("");
        }
        log.info("Ending QR callback");
        return connectorResponse;
    }

    @Override
    public Object qrStatusCheck(String token, String merchantId) throws CustomException {
        Map<String, Object> resp = new HashMap<>();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository
                    .findByMerchantTranId(merchantId);
            if (digitalPaymentTransactionsEntity != null) {
                resp.put(STATUS, digitalPaymentTransactionsEntity.getStatus());
            } else {
                throw new CustomException("");
            }

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
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
    @Transactional(rollbackOn = Exception.class)
    public Object digitalTransactionStatusCheck(String token, CommonTransactionStatusCheckRequestDTO requestBody)
            throws CustomException, InterruptedException {
        try {
            return this.getQrCodeTransactionStatus(token, requestBody);
        } catch(InterruptedException ie) {
            log.error(INTERRUPTED_EXCEPTION_STR, ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        } catch (Exception ee) {
            throw new CustomException(ee.getMessage());
        }
    }

    private DynamicQrCodeCheckStatusDataResponseDTO settingResponseData() {
        return DynamicQrCodeCheckStatusDataResponseDTO.builder()
                .status("qr_expired")
                .build();
    }

    private int findDayEndRemainingMinute() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime endOfDay = LocalTime.of(23, 59);
        LocalTime currentTime = now.toLocalTime();
        Duration duration = Duration.between(currentTime, endOfDay);
        return (int) duration.toMinutes();
    }
}
