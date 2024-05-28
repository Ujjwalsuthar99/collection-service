package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkCollectionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs.PaymentLinkRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.PaymentLinkResponseDTOs.PaymentLinkDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.PaymentLinkResponseDTOs.PaymentLinkResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.PaymentLinkService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;

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
    UtilityService utilityService;

    @Override
    @Transactional
    public Object sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws Exception {
        PaymentLinkCollectionRequestDTO paymentLinkCollectionRequestDTO;
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<PaymentLinkResponseDTO> res;
        if (data instanceof PaymentLinkCollectionRequestDTO) {
            paymentLinkCollectionRequestDTO = (PaymentLinkCollectionRequestDTO) data;
        } else {
            JsonNode jsonNode = objectMapper.readTree(String.valueOf(data));
            paymentLinkCollectionRequestDTO = objectMapper.convertValue(jsonNode, PaymentLinkCollectionRequestDTO.class);
        }

        ReceiptServiceDtoRequest receiptServiceDtoRequest = objectMapper.convertValue(paymentLinkCollectionRequestDTO.getReceiptBody(), ReceiptServiceDtoRequest.class);
        long loanId = Long.parseLong(receiptServiceDtoRequest.getRequestData().getLoanId());
        PaymentLinkDataRequestDTO paymentLinkDataRequestDTO = new PaymentLinkDataRequestDTO(
                "true",
                loanAllocationRepository.getProductType(loanId),
                Integer.parseInt(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()),
                "INR",
                false,
                100,
                DateUtils.addMinutes(new Date(), 15).getTime(),
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

            res = restTemplate.exchange(
                    SEND_PAYMENT_LINK,
                    HttpMethod.POST,
                    new HttpEntity<>(paymentLinkRequestDTO, httpHeaders),
                    PaymentLinkResponseDTO.class);

            Assert.notNull(res.getBody().getData(), res.getBody().getError().getMessage());
            
            String activityRemarks = "Payment link sent against loan id " + loanId + " of payment Rs. " + receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount();
            CollectionActivityLogsEntity collectionActivityLogsEntity = getCollectionActivityLogsEntity("send_payment_link", receiptServiceDtoRequest.getActivityData().getUserId(), loanId, activityRemarks, receiptServiceDtoRequest.getActivityData().getGeolocationData());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            String merchantTranId = loanId + "_" + System.currentTimeMillis();
            createDigitalPaymentLinkTransaction(receiptServiceDtoRequest, paymentLinkCollectionRequestDTO.getMobileNumber(), merchantTranId, collectionActivityLogsEntity.getCollectionActivityLogsId(), paymentLinkCollectionRequestDTO.getVendor(), res.getBody());


            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, null, paymentLinkRequestDTO, res, "success", loanId, HttpMethod.POST.name(), "sendPaymentLink");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_payment_link, null, paymentLinkRequestDTO, modifiedErrorMessage, "failure", loanId, HttpMethod.POST.name(), "sendPaymentLink");
            log.error("{}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        return res.getBody();
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
