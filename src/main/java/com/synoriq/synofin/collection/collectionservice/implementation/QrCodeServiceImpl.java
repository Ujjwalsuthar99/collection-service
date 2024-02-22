package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DigitalPaymentTransactionsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDataDTO;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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


    @Override
    public DynamicQrCodeResponseDTO sendQrCode(String token, DynamicQrCodeRequestDTO requestBody) throws Exception {
        DynamicQrCodeResponseDTO res = new DynamicQrCodeResponseDTO();
        DynamicQrCodeDataRequestDTO integrationDataRequestBody = new DynamicQrCodeDataRequestDTO();
        DynamicQrCodeIntegrationDataRequestDTO integrationRequestBody = new DynamicQrCodeIntegrationDataRequestDTO();

        integrationDataRequestBody.setAmount(String.valueOf(requestBody.getAmount()));
        integrationDataRequestBody.setPayerAccount(requestBody.getPayerAccount());
        integrationDataRequestBody.setPayerIFSC(requestBody.getPayerIFSC());
        integrationDataRequestBody.setFirstName(requestBody.getFirstName());
        integrationDataRequestBody.setLastName(requestBody.getLastName());
        String billNumber = null;
        String merchantTransId = null;
        if (requestBody.getVendor().equals("kotak")) {
            billNumber = "." + requestBody.getLoanId() + "." + System.currentTimeMillis();
            merchantTransId = "." + requestBody.getLoanId() + "." + System.currentTimeMillis();
            integrationDataRequestBody.setBillNumber(billNumber);
            integrationDataRequestBody.setMerchantTranId(merchantTransId);
        } else {
            billNumber = requestBody.getLoanId() + "_" + System.currentTimeMillis();
            merchantTransId = requestBody.getLoanId() + "_" + System.currentTimeMillis();
            integrationDataRequestBody.setBillNumber(billNumber);
            integrationDataRequestBody.setMerchantTranId(merchantTransId);
        }

        integrationRequestBody.setDynamicQrCodeDataRequestDTO(integrationDataRequestBody);
        integrationRequestBody.setSystemId("collection");
        integrationRequestBody.setUserReferenceNumber(String.valueOf(requestBody.getUserId()));
        integrationRequestBody.setSpecificPartnerName(requestBody.getVendor());

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");


            res = HTTPRequestService.<Object, DynamicQrCodeResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/sendQrCode")
                    .httpHeaders(httpHeaders)
                    .body(integrationRequestBody)
                    .typeResponseType(DynamicQrCodeResponseDTO.class)
                    .build().call();

            DynamicQrCodeDataResponseDTO dynamicQrCodeDataResponseDTO = new DynamicQrCodeDataResponseDTO();
            dynamicQrCodeDataResponseDTO.setMerchantTranId(merchantTransId);
            dynamicQrCodeDataResponseDTO.setLink(res.getData().getLink());
            dynamicQrCodeDataResponseDTO.setStatus(res.getData().getStatus());

            DynamicQrCodeResponseDTO dynamicQrCodeResponseDto = new DynamicQrCodeResponseDTO();
            dynamicQrCodeResponseDto.setResponse(res.getResponse());
            dynamicQrCodeResponseDto.setRequestId(res.getRequestId());
            dynamicQrCodeResponseDto.setData(dynamicQrCodeDataResponseDTO);
            res = dynamicQrCodeResponseDto;

            if (res.getResponse().equals(true)) {

                CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
                collectionActivityLogsEntity.setActivityName("generated_dynamic_qr_code");
                collectionActivityLogsEntity.setActivityDate(new Date());
                collectionActivityLogsEntity.setDeleted(false);
                collectionActivityLogsEntity.setActivityBy(requestBody.getUserId());
                collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
                collectionActivityLogsEntity.setAddress("{}");
                collectionActivityLogsEntity.setRemarks("Generated a QR code against loan id " + requestBody.getLoanId() + " of payment Rs. " + requestBody.getAmount());
                collectionActivityLogsEntity.setImages("{}");
                collectionActivityLogsEntity.setLoanId(requestBody.getLoanId());
                collectionActivityLogsEntity.setGeolocation(requestBody.getGeolocation());

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
                digitalPaymentTransactionsEntity.setPaymentServiceName("dynamic_qr_code");
                digitalPaymentTransactionsEntity.setStatus("pending");
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, null, integrationRequestBody, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, null, integrationRequestBody, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DynamicQrCodeCheckStatusResponseDTO getQrCodeTransactionStatus(String token, DynamicQrCodeStatusCheckRequestDTO requestBody) throws Exception {
        DynamicQrCodeCheckStatusResponseDTO res = new DynamicQrCodeCheckStatusResponseDTO();
        Map<String, Object> response = new HashMap<>();
        DynamicQrCodeStatusCheckIntegrationRequestDTO dynamicQrCodeStatusCheckIntegrationRequestDTO = new DynamicQrCodeStatusCheckIntegrationRequestDTO();
        DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO = new DynamicQrCodeStatusCheckDataRequestDTO();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntityData = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());
            dynamicQrCodeStatusCheckDataRequestDTO.setMerchantTranId(requestBody.getMerchantTranId());
            dynamicQrCodeStatusCheckDataRequestDTO.setCustomerId("91" + digitalPaymentTransactionsEntityData.getMobileNo().toString());

            dynamicQrCodeStatusCheckIntegrationRequestDTO.setDynamicQrCodeStatusCheckDataRequestDTO(dynamicQrCodeStatusCheckDataRequestDTO);
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setUserReferenceNumber(String.valueOf(digitalPaymentTransactionsEntityData.getCreatedBy()));
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setSystemId("collection");
            dynamicQrCodeStatusCheckIntegrationRequestDTO.setSpecificPartnerName(digitalPaymentTransactionsEntityData.getVendor());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

//            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());

//            if(digitalPaymentTransactionsEntity.getStatus().equals("success")) {
//                throw new Exception("1016045");
//            }

            res = HTTPRequestService.<Object, DynamicQrCodeCheckStatusResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getQrCodeTransactionStatus")
                    .httpHeaders(httpHeaders)
                    .body(dynamicQrCodeStatusCheckIntegrationRequestDTO)
                    .typeResponseType(DynamicQrCodeCheckStatusResponseDTO.class)
                    .build().call();


            CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
            collectionActivityLogsEntity.setActivityName("dynamic_qr_code_payment_" + res.getData().getStatus().toLowerCase());
            collectionActivityLogsEntity.setActivityDate(new Date());
            collectionActivityLogsEntity.setDeleted(false);
            collectionActivityLogsEntity.setActivityBy(digitalPaymentTransactionsEntityData.getCreatedBy());
            collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
            collectionActivityLogsEntity.setAddress("{}");
            collectionActivityLogsEntity.setRemarks("The payment status for transaction id " + requestBody.getDigitalPaymentTransactionId() + " and loan id " + digitalPaymentTransactionsEntityData.getLoanId() + " has been updated as" + res.getData().getStatus().toLowerCase() + " by checking the status manually");
            collectionActivityLogsEntity.setImages("{}");
            collectionActivityLogsEntity.setLoanId(digitalPaymentTransactionsEntityData.getLoanId());
            collectionActivityLogsEntity.setGeolocation(requestBody.getGeolocation());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            if (res.getData().getStatus().equals("success")) {
                if (!digitalPaymentTransactionsEntityData.getReceiptGenerated()) {
                    createReceiptByCallBack(digitalPaymentTransactionsEntityData, token, response);
                } else {
                    Map<String, Object> respMap = new ObjectMapper().convertValue(digitalPaymentTransactionsEntityData.getReceiptResponse(), Map.class);
                    response.put("status", res.getData().getStatus().toLowerCase());
                    response.put("receipt_generated", true);
                    response.put("service_request_id", String.valueOf(respMap.get("service_request_id")));
                }
            } else {
                response.put("status", "failure");
                response.put("receipt_generated", digitalPaymentTransactionsEntityData.getReceiptGenerated());
                response.put("service_request_id", null);
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
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws Exception {
        String merchantTransId = requestBody.getMerchantTranId();
        Map<String, Object> response = new HashMap<>();
        Long loanId = null;
        boolean isSuccess = false;
        try {
            log.info("hurray! callback received for QR");
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByMerchantTranId(merchantTransId);
            if (digitalPaymentTransactionsEntity != null) {
                if (Objects.equals(requestBody.getStatus(), "SUCCESS")) {
                    digitalPaymentTransactionsEntity.setStatus("success");
                    digitalPaymentTransactionsEntity.setUtrNumber(requestBody.getOriginalBankRRN());
                    // calling create receipt function for call back
                    createReceiptByCallBack(digitalPaymentTransactionsEntity, token, response);
                }
                loanId = digitalPaymentTransactionsEntity.getLoanId();
                digitalPaymentTransactionsEntity.setCallBackRequestBody(requestBody);
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);

                CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
                collectionActivityLogsEntity.setActivityName("dynamic_qr_code_payment_" + requestBody.getStatus().toLowerCase());
                collectionActivityLogsEntity.setActivityDate(new Date());
                collectionActivityLogsEntity.setDeleted(false);
                collectionActivityLogsEntity.setActivityBy(digitalPaymentTransactionsEntity.getCreatedBy());
                collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
                collectionActivityLogsEntity.setAddress("{}");
                collectionActivityLogsEntity.setRemarks("The payment status for transaction id " + digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId() + " and loan id " + digitalPaymentTransactionsEntity.getLoanId() + " has been updated as success");
                collectionActivityLogsEntity.setImages("{}");
                collectionActivityLogsEntity.setLoanId(digitalPaymentTransactionsEntity.getLoanId());
                collectionActivityLogsEntity.setGeolocation("{}");

                collectionActivityLogsRepository.save(collectionActivityLogsEntity);
                response.put("status", requestBody.getStatus().toLowerCase());
            } else {
                response.put("status", null);
                response.put("receipt_generated", null);
                response.put("service_request_id", null);

            }
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, response, "success", loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("errorMessage {}", errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.qr_callback, null, requestBody, errorMessage, "failure", loanId);
            throw new Exception();
        }
        response.remove("receipt_generated");
        response.remove("service_request_id");
        return response.replace("status", isSuccess);
    }

    private void createReceiptByCallBack(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity, String token, Map<String, Object> response) throws Exception {
        log.info("in callback create receipt function starting");
        try {
            CurrentUserInfo currentUserInfo = new CurrentUserInfo();
            // implementing create receipt here
            ReceiptServiceDtoRequest receiptServiceDtoRequest = new ObjectMapper().convertValue(digitalPaymentTransactionsEntity.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            ServiceRequestSaveResponse resp = receiptService.createReceipt(receiptServiceDtoRequest, token, true);
            digitalPaymentTransactionsEntity.setReceiptResponse(resp.getData());
            if (resp.getData() != null && resp.getData().getServiceRequestId() != null) {
                response.put("receipt_generated", true);
                response.put("service_request_id", resp.getData().getServiceRequestId());
                digitalPaymentTransactionsEntity.setReceiptGenerated(true);
                String url = "http://localhost:1102/v1/getPdf?deliverableType=receipt_details&serviceRequestId=" + resp.getData().getServiceRequestId();

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
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public Object qrStatusCheck(String token, String merchantId) throws Exception {
        Map<String, Object> resp = new HashMap<>();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByMerchantTranId(merchantId);
            if (digitalPaymentTransactionsEntity != null) {
                resp.put("status", digitalPaymentTransactionsEntity.getStatus());
            } else {
                throw new Exception("");
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return new BaseDTOResponse<>(resp);
    }

}
