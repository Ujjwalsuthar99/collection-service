package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos.CommonTransactionStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.PaymentLinkCollectionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.PaymentLinkDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.PaymentLinkRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.statuscheckdtos.TransactionStatusCheckDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.statuscheckdtos.TransactionStatusCheckDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos.PaymentLinkResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos.TransactionStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos.TransactionStatusResponseDataDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;


@Service
@Slf4j
public class PaymentLinkServiceImpl implements PaymentLinkService, DigitalTransactionChecker {

    private static final String STATUS_MESSAGE = "success";
    private static final String FAILURE_STATUS = "failure";
    private static final String EXPIRED_STATUS = "expired";
    public PaymentLinkServiceImpl(CollectionActivityLogsRepository collectionActivityLogsRepository,
                                  DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository,
                                  LoanAllocationRepository loanAllocationRepository,
                                  ConsumedApiLogService consumedApiLogService,
                                  CollectionConfigurationsRepository collectionConfigurationsRepository,
                                  UtilityService utilityService,
                                  IntegrationConnectorService integrationConnectorService,
                                  RestTemplate restTemplate) {
        this.integrationConnectorService = integrationConnectorService;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
        this.utilityService = utilityService;
        this.consumedApiLogService = consumedApiLogService;
        this.digitalPaymentTransactionsRepository = digitalPaymentTransactionsRepository;
        this.collectionActivityLogsRepository = collectionActivityLogsRepository;
        this.loanAllocationRepository = loanAllocationRepository;
        this.restTemplate = restTemplate;

    }
    private final CollectionActivityLogsRepository collectionActivityLogsRepository;
    private final DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    private final LoanAllocationRepository loanAllocationRepository;
    private final ConsumedApiLogService consumedApiLogService;
    private final IntegrationConnectorService integrationConnectorService;
    private final CollectionConfigurationsRepository collectionConfigurationsRepository;
    private final UtilityService utilityService;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public BaseDTOResponse<Object> sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws ConnectorException, JsonProcessingException, InterruptedException, ExecutionException {
        PaymentLinkCollectionRequestDTO paymentLinkCollectionRequestDTO;
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentLinkResponseDTO res = new PaymentLinkResponseDTO();
        if (data instanceof PaymentLinkCollectionRequestDTO) {
            paymentLinkCollectionRequestDTO = (PaymentLinkCollectionRequestDTO) data;
        } else {
            JsonNode jsonNode = objectMapper.readTree(String.valueOf(data));
            paymentLinkCollectionRequestDTO = objectMapper.convertValue(jsonNode, PaymentLinkCollectionRequestDTO.class);
        }

        ReceiptServiceDtoRequest receiptServiceDtoRequest = objectMapper.convertValue(paymentLinkCollectionRequestDTO.getReceiptBody(), ReceiptServiceDtoRequest.class);
        receiptServiceDtoRequest.getRequestData().getRequestData().setPaymentMode("upi");
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

        long loanId = Long.parseLong(receiptServiceDtoRequest.getRequestData().getLoanId());
        int minutes = Integer.parseInt(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(PAYMENT_LINK_EXPIRATION_CONF));

        PaymentLinkDataRequestDTO paymentLinkDataRequestDTO = new PaymentLinkDataRequestDTO(
                "true",
                loanAllocationRepository.getProductType(loanId),
                Integer.parseInt(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()),
                "INR",
                false,
                100,
                Instant.now().plus(minutes, ChronoUnit.MINUTES).getEpochSecond(),
                receiptServiceDtoRequest.getRequestData().getRequestData().getRemarks(),
                paymentLinkCollectionRequestDTO.getCustomerName(),
                paymentLinkCollectionRequestDTO.getMobileNumber(),
                "",
                "https://example-callback-url.com/",
                "get");

        PaymentLinkRequestDTO paymentLinkRequestDTO = new PaymentLinkRequestDTO();
        paymentLinkRequestDTO.setPaymentLinkDataRequestDTO(paymentLinkDataRequestDTO);
        paymentLinkRequestDTO.setSystemId(COLLECTION);
        paymentLinkRequestDTO.setUserReferenceNumber("");
        paymentLinkRequestDTO.setSpecificPartnerName(RAZORPAY);

        String paymentLinkEndPoint = "sendPaymentLink";
        try {

            HttpHeaders httpHeaders = UtilityService.createHeaders(token);

            res = HTTPRequestService.<Object, PaymentLinkResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_PAYMENT_LINK)
                    .httpHeaders(httpHeaders)
                    .body(paymentLinkRequestDTO)
                    .typeResponseType(PaymentLinkResponseDTO.class)
                    .build().call(restTemplate);

            if (res.getData() == null) {
                throw new CustomException(res.toString());
            }
            String activityRemarks = "Payment link sent against loan id " + loanId + " of payment Rs. " + receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount();
            CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity("send_payment_link", receiptServiceDtoRequest.getActivityData().getUserId(), loanId, activityRemarks, receiptServiceDtoRequest.getActivityData().getGeolocationData(), receiptServiceDtoRequest.getActivityData().getBatteryPercentage());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            // creating digital payment transaction entry
            createDigitalPaymentLinkTransaction(receiptServiceDtoRequest, paymentLinkCollectionRequestDTO.getMobileNumber(), res.getData().getId(), collectionActivityLogsEntity.getCollectionActivityLogsId(), collectionConfigurationsRepository.findConfigurationValueByConfigurationName(PAYMENT_LINK_VENDOR), res);

            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getUserId(), paymentLinkRequestDTO, res, STATUS_MESSAGE, loanId, HttpMethod.POST.name(), paymentLinkEndPoint);
        } catch (ConnectorException ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getUserId(), paymentLinkRequestDTO, modifiedErrorMessage, FAILURE_STATUS, loanId, HttpMethod.POST.name(), paymentLinkEndPoint);
            throw new ConnectorException(ErrorCode.S3_UPLOAD_DATA_ERROR, ee.getText(), HttpStatus.FAILED_DEPENDENCY, ee.getRequestId());
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getUserId(), paymentLinkRequestDTO, modifiedErrorMessage, FAILURE_STATUS, loanId, HttpMethod.POST.name(), paymentLinkEndPoint);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            IntegrationServiceErrorResponseDTO r = new ObjectMapper().readValue(ow.writeValueAsString(res.getError()), IntegrationServiceErrorResponseDTO.class);
            throw new ConnectorException(r, HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
        }
        return new BaseDTOResponse<>("Payment Link has been sent to the customer, Link will be expired in next " + minutes + " minutes");
    }


    public void createDigitalPaymentLinkTransaction(ReceiptServiceDtoRequest receiptServiceDtoRequest, String mobileNumber, String merchantTransId, Long activityId, String vendor, PaymentLinkResponseDTO otherResponseData) {
        DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = new DigitalPaymentTransactionsEntity();
        digitalPaymentTransactionsEntity.setCreatedDate(new Date());
        digitalPaymentTransactionsEntity.setCreatedBy(receiptServiceDtoRequest.getActivityData().getUserId());
        digitalPaymentTransactionsEntity.setModifiedDate(null);
        digitalPaymentTransactionsEntity.setModifiedBy(null);
        digitalPaymentTransactionsEntity.setLoanId(Long.parseLong(receiptServiceDtoRequest.getRequestData().getLoanId()));
        digitalPaymentTransactionsEntity.setPaymentServiceName(PAYMENT_LINK);
        digitalPaymentTransactionsEntity.setStatus(PENDING);
        digitalPaymentTransactionsEntity.setMerchantTranId(merchantTransId);
        digitalPaymentTransactionsEntity.setAmount(Float.parseFloat(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()));
        digitalPaymentTransactionsEntity.setUtrNumber(null);
        digitalPaymentTransactionsEntity.setReceiptRequestBody(receiptServiceDtoRequest);
        digitalPaymentTransactionsEntity.setPaymentLink(otherResponseData.getData().getUrl());
        digitalPaymentTransactionsEntity.setMobileNo(Long.parseLong(mobileNumber));
        digitalPaymentTransactionsEntity.setVendor(vendor);
        digitalPaymentTransactionsEntity.setReceiptGenerated(false);
        digitalPaymentTransactionsEntity.setCollectionActivityLogsId(activityId);
        digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
        digitalPaymentTransactionsEntity.setOtherResponseData(otherResponseData);

        digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
    }

    @Override
    @Transactional
    public Object getPaymentTransactionStatus(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws CustomException, JsonProcessingException, InterruptedException {
        Map<String, Object> response = new HashMap<>();
        TransactionStatusResponseDTO res = new TransactionStatusResponseDTO();
        DigitalPaymentTransactionsEntity digitalPaymentTransactions = digitalPaymentTransactionsRepository.findByMerchantTranId(requestBody.getMerchantTranId());
        Long loanId = digitalPaymentTransactions.getLoanId();
        TransactionStatusCheckDataDTO transactionStatusCheckDataDTO = TransactionStatusCheckDataDTO.builder()
                .id(requestBody.getMerchantTranId())
                .productType(loanAllocationRepository.getProductType(digitalPaymentTransactions.getLoanId()))
                .build();
        TransactionStatusCheckDTO transactionStatusCheckDTO = TransactionStatusCheckDTO.builder()
                .transactionStatusCheckDataDTO(transactionStatusCheckDataDTO)
                .systemId(COLLECTION)
                .specificPartnerName(RAZORPAY)
                .userReferenceNumber("")
                .build();

        // Adding Validation here //
        ConsumedApiLogsEntity consumedApiLogsEntity = consumedApiLogService.getLastDataByLoanIdAndLogName(loanId, EnumSQLConstants.LogNames.check_payment_link_status);
        if (consumedApiLogsEntity != null && utilityService.isExpired(10, consumedApiLogsEntity.getCreatedDate(), false)) {
            ErrorCode errorCode = ErrorCode.getErrorCode(1016058, "Check status will be available at " + utilityService.addMinutes(10, consumedApiLogsEntity.getCreatedDate()));
            throw new CustomException(errorCode);
        }
        String statusCheckEndPoint = "paymentLinkTransactionStatusCheck";
        try {

            res = HTTPRequestService.<Object, TransactionStatusResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(PAYMENT_LINK_TRANSACTION_CHECK)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(transactionStatusCheckDTO)
                    .typeResponseType(TransactionStatusResponseDTO.class)
                    .build().call(restTemplate);

            if (Objects.requireNonNull(res).getData() == null) {
                throw new ConnectorException(res.toString());
            }
            String activityRemarks = "The payment status for transaction id " + digitalPaymentTransactions.getDigitalPaymentTransactionsId() + " and loan id " + loanId + " has been updated as " + res.getData().getStatus().toLowerCase();
            String activityName = "status_of_payment_link_" + res.getData().getStatus().toLowerCase();
            CollectionActivityLogsEntity collectionActivityLogsEntity = utilityService.getCollectionActivityLogsEntity(activityName, digitalPaymentTransactions.getCreatedBy(), loanId, activityRemarks, "{}", 90L);
            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            // dummy resppnse here
            // dummy resppinse here
            if (res.getData().getStatus().equalsIgnoreCase(PAID)) {
                if (Boolean.FALSE.equals(digitalPaymentTransactions.getReceiptGenerated())) {
                    log.info("receipt generate check {}", res);
                    utilityService.createReceiptByCallBack(digitalPaymentTransactions, token, response, res.getData().getOrderId());
                    log.info("create receipt done");
                } else {
                    Map<String, Object> respMap = new ObjectMapper().convertValue(digitalPaymentTransactions.getReceiptResponse(), Map.class);
                    response.put(STATUS, res.getData().getStatus().toLowerCase());
                    response.put(RECEIPT_GENERATED, true);
                    response.put(SR_ID, String.valueOf(respMap.get("service_request_id")));
                }
            } else {
                log.info("in else");
                response.put(STATUS, FAILURE);
                response.put(RECEIPT_GENERATED, digitalPaymentTransactions.getReceiptGenerated());
                response.put(SR_ID, null);
                int expiration = Integer.parseInt(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(PAYMENT_LINK_EXPIRATION_CONF));
                if ((res.getData().getStatus().equalsIgnoreCase(PENDING) || res.getData().getStatus().equalsIgnoreCase(EXPIRED)) && utilityService.isExpired(expiration, digitalPaymentTransactions.getCreatedDate(), true)) {
                    log.info("hrere");
                    digitalPaymentTransactions.setStatus(EXPIRED_STATUS);
                    digitalPaymentTransactionsRepository.save(digitalPaymentTransactions);
                    log.info("expired response -> {}", settingResponseData());
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_payment_link_status, digitalPaymentTransactions.getCreatedBy(), transactionStatusCheckDTO, TransactionStatusResponseDataDTO.builder().status(EXPIRED_STATUS).orderId(null).build(), STATUS_MESSAGE, loanId, HttpMethod.POST.name(), statusCheckEndPoint);
                    return settingResponseData();
                }
            }
            digitalPaymentTransactions.setUtrNumber(res.getData().getOrderId());
            digitalPaymentTransactions.setStatus(res.getData().getStatus().toLowerCase());
            digitalPaymentTransactions.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());

            digitalPaymentTransactionsRepository.save(digitalPaymentTransactions);
            res.getData().setStatus(res.getData().getStatus().toLowerCase());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_payment_link_status, digitalPaymentTransactions.getCreatedBy(), transactionStatusCheckDTO, res, STATUS_MESSAGE, loanId, HttpMethod.POST.name(), statusCheckEndPoint);
        } catch (CustomException ee) {
            throw new CustomException(ee.getMessage(), ee.getCode());
        } catch (ConnectorException ee) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            IntegrationServiceErrorResponseDTO r = new ObjectMapper().readValue(ow.writeValueAsString(res.getError()), IntegrationServiceErrorResponseDTO.class);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_payment_link_status, digitalPaymentTransactions.getCreatedBy(), transactionStatusCheckDTO, res, FAILURE_STATUS, loanId, HttpMethod.POST.name(), statusCheckEndPoint);
            throw new ConnectorException(r, HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
        } catch(InterruptedException ie) {
            log.error("Interrupted Exception Error {}", ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_payment_link_status, digitalPaymentTransactions.getCreatedBy(), transactionStatusCheckDTO, utilityService.convertToJSON(ee.getMessage()), FAILURE_STATUS, loanId, HttpMethod.POST.name(), statusCheckEndPoint);
            throw new CustomException(ee.getMessage());
        }
        return res.getData();
    }
    @Override
    @Transactional()
    public Object digitalTransactionStatusCheck(String token, CommonTransactionStatusCheckRequestDTO requestBody) throws CustomException, InterruptedException {
        try{
    
            return this.getPaymentTransactionStatus(token, requestBody);
        }  catch(InterruptedException ie) {
            log.error("Interrupted Exception Error {}", ie.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ie.getMessage());
        }  catch(Exception ee){
            throw new CustomException(ee.getMessage());
        }
    }

    private TransactionStatusResponseDataDTO settingResponseData() {
        return TransactionStatusResponseDataDTO.builder().status(EXPIRED_STATUS).orderId(null).build();
    }
}
