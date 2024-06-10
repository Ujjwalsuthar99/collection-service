package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkCollectionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.PaymentLinkResponseDTOs.PaymentLinkResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.IntegrationConnectorService;
import com.synoriq.synofin.collection.collectionservice.service.PaymentLinkService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.PAYMENT_LINK_EXPIRATION_CONF;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;
import static com.synoriq.synofin.collection.collectionservice.implementation.QrCodeServiceImpl.getCollectionActivityLogsEntity;


@Service
@Slf4j
public class PaymentLinkServiceImpl implements PaymentLinkService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;

    @Autowired
    LoanAllocationRepository loanAllocationRepository;

    @Autowired
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    IntegrationConnectorService integrationConnectorService;

    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Autowired
    UtilityService utilityService;

    @Override
    @Transactional
    public BaseDTOResponse<Object> sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws ConnectorException, Exception {
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
        paymentLinkRequestDTO.setSystemId("collection");
        paymentLinkRequestDTO.setUserReferenceNumber("");
        paymentLinkRequestDTO.setSpecificPartnerName("razorpay");

        try {

            HttpHeaders httpHeaders = UtilityService.createHeaders(token);

            res = HTTPRequestService.<Object, PaymentLinkResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(SEND_PAYMENT_LINK)
                    .httpHeaders(httpHeaders)
                    .body(paymentLinkRequestDTO)
                    .typeResponseType(PaymentLinkResponseDTO.class)
                    .build().call();

            if (Objects.requireNonNull(res).getData() == null) {
                throw new Exception(res.toString());
            }
//            Assert.notNull(res.getBody().getData(), res.getBody().getError().toString());
            String activityRemarks = "Payment link sent against loan id " + loanId + " of payment Rs. " + receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount();
            CollectionActivityLogsEntity collectionActivityLogsEntity = getCollectionActivityLogsEntity("send_payment_link", receiptServiceDtoRequest.getActivityData().getUserId(), loanId, activityRemarks, receiptServiceDtoRequest.getActivityData().getGeolocationData());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            String merchantTranId = loanId + "_" + System.currentTimeMillis();
            // creating digital payment transaction entry
            createDigitalPaymentLinkTransaction(receiptServiceDtoRequest, paymentLinkCollectionRequestDTO.getMobileNumber(), merchantTranId, collectionActivityLogsEntity.getCollectionActivityLogsId(), paymentLinkCollectionRequestDTO.getVendor(), res);

            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getLoanId(), paymentLinkRequestDTO, res, "success", loanId, HttpMethod.POST.name(), "sendPaymentLink");
        } catch (ConnectorException ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getLoanId(), paymentLinkRequestDTO, modifiedErrorMessage, "failure", loanId, HttpMethod.POST.name(), "sendPaymentLink");
            throw new ConnectorException(ErrorCode.S3_UPLOAD_DATA_ERROR, ee.getText(), HttpStatus.FAILED_DEPENDENCY, ee.getRequestId());
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, receiptServiceDtoRequest.getActivityData().getLoanId(), paymentLinkRequestDTO, modifiedErrorMessage, "failure", loanId, HttpMethod.POST.name(), "sendPaymentLink");
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            IntegrationServiceErrorResponseDTO r = new ObjectMapper().readValue(ow.writeValueAsString(res.getError()), IntegrationServiceErrorResponseDTO.class);
            throw new ConnectorException(r, HttpStatus.FAILED_DEPENDENCY, res.getRequestId());
        }
        return new BaseDTOResponse<>("Payment Link has been sent to the customer, Link will be expired in next " + minutes + " minutes");
    }

    @NotNull
    public void createDigitalPaymentLinkTransaction(ReceiptServiceDtoRequest receiptServiceDtoRequest, String mobileNumber, String merchantTransId, Long activityId, String vendor, Object otherResponseData) {
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
        digitalPaymentTransactionsEntity.setPaymentLink(null);
        digitalPaymentTransactionsEntity.setMobileNo(Long.parseLong(mobileNumber));
        digitalPaymentTransactionsEntity.setVendor(vendor);
        digitalPaymentTransactionsEntity.setReceiptGenerated(false);
        digitalPaymentTransactionsEntity.setCollectionActivityLogsId(activityId);
        digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
        digitalPaymentTransactionsEntity.setOtherResponseData(otherResponseData);

        digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
    }

}
