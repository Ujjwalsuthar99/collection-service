package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.implementations.ReceiptServiceImpl;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import com.synoriq.synofin.collection.collectionservice.rest.response.createReceiptLms.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties.GetReceiptDateResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties.ReceiptDateResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties.ReceiptServiceSystemPropertiesResponse;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.FinovaSmsService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.CREATE_RECEIPT;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class ReceiptService {

    @Autowired
    ReceiptRepository receiptRepository;

    @Autowired
    private LoanAllocationRepository loanAllocationRepository;

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private CollectionReceiptRepository collectionReceiptRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    private CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FinovaSmsService finovaSmsService;

    @Autowired
    private CurrentUserInfo currentUserInfo;


    public BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String status, String paymentMode, Integer page, Integer size) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            paymentMode = paymentMode == "" ? paymentMode = "cash" : paymentMode;
            Pageable pageRequest;
            if (page > 0) {
                page = page - 1;
            }
            pageRequest = PageRequest.of(page, size);
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByUserIdWithDuration(userName, fromDate, toDate, pageRequest);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }


    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Map<String, Object>> receiptsData = receiptRepository.getReceiptsByUserIdWhichNotTransferred(userName);
            baseDTOResponse = new BaseDTOResponse<>(receiptsData);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    public BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            paymentMode = paymentMode == "" ? paymentMode = "cash" : paymentMode;
            //            Pageable pageRequest;
            //            if (pageNo > 0) {
            //                pageNo = pageNo - 1;
            //            }
            //            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByLoanIdWithDuration(loanId, fromDate, toDate);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }
    public BaseDTOResponse<Object> getReceiptsBySearchKey(String userName, String searchKey, Integer page, Integer size) throws Exception {
        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (page > 0) {
                page = page - 1;
            }
            pageRequest = PageRequest.of(page, size);
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsBySearchKey(userName, searchKey, pageRequest);
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }
        return baseDTOResponse;
    }

    @Transactional
    public ServiceRequestSaveResponse createReceipt(@RequestBody ReceiptServiceDtoRequest receiptServiceDtoRequest, String bearerToken) throws Exception {
        ServiceRequestSaveResponse res;
        Long collectionActivityId = null;
        ReceiptServiceDtoRequest createReceiptBody = new ObjectMapper().convertValue(receiptServiceDtoRequest, ReceiptServiceDtoRequest.class);

        ReceiptServiceRequestDataDTO receiptServiceRequestDataDTO = new ReceiptServiceRequestDataDTO();
        try {
            // always in minutes
            long validationTime = Long.parseLong(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RECEIPT_TIME_VALIDATE));

            // check for duplicate receipt generate under 10 min
            Map<String, Object> createReceiptTimeError = receiptRepository.getReceiptData(Long.parseLong(createReceiptBody.getRequestData().getLoanId()), createReceiptBody.getRequestData().getRequestData().getReceiptAmount());
            if (createReceiptTimeError.size() != 0) {
                String dateTime = String.valueOf(createReceiptTimeError.get("created_date")); // 2023-05-18 18:23:30.292
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = dateFormat.parse(dateTime);
                Date currentDateTime = new Date();
                long timeDifference = (currentDateTime.getTime() - newDate.getTime()) / (60 * 1000);
                if (timeDifference < validationTime) {
                    throw new Exception("1016038");
                }
            }
            // check for duplicate transaction reference number
            if (receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi")) {
                Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(receiptServiceDtoRequest.getRequestData().getRequestData().getTransactionReference());
                if (transactionNumberCheck.size() != 0) {
                    throw new Exception("1016039");
                }
            }

            String limitConf = null;
            if(receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cash")) {
                limitConf = CASH_COLLECTION_DEFAULT_LIMIT;
            } else if(receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cheque")) {
                limitConf = CHEQUE_COLLECTION_DEFAULT_LIMIT;
            } else if(receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi"))  {
                limitConf = ONLINE_COLLECTION_DEFAULT_LIMIT;
            }


            Double totalLimitValue = 0.00;
            Double currentReceiptAmountAllowed = 0.00;
            double receiptAmount = Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());
            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode);

            if(collectionLimitUser != null) {
                totalLimitValue = collectionLimitUser.getTotalLimitValue();
                currentReceiptAmountAllowed = totalLimitValue - collectionLimitUser.getUtilizedLimitValue();
                log.info("Utilized limit {}", collectionLimitUser.getUtilizedLimitValue());
            } else {
                currentReceiptAmountAllowed = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf));
            }

            // per day cash limit check
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash")) {
                double perDayCashLimitLoan = Double.parseDouble(collectionConfigurationsRepository.findConfigurationValueByConfigurationName("per_day_cash_collection_customer_limit"));
                double receiptCollectedAmountTillToday = receiptRepository.getCollectedAmountToday(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()));

                log.info("perDayCashLimitLoan {}", perDayCashLimitLoan);
                log.info("receiptCollectedAmountTillToday {}", receiptCollectedAmountTillToday);
                log.info("receiptAmount {}", receiptAmount);
                if (receiptCollectedAmountTillToday + receiptAmount > perDayCashLimitLoan) {
                    throw new Exception("1017005");
                }
            }
            log.info("Total Limit Value {}", totalLimitValue);
            log.info("Receipt amount can be collected by a user at current situation {}", currentReceiptAmountAllowed);
            log.info("Receipt amount {}", receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());

            if(currentReceiptAmountAllowed < Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount())) {
                throw new Exception("1017003");
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");



            String bDate = receiptServiceDtoRequest.getRequestData().getRequestData().getDateOfReceipt();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = simpleDateFormat.parse(bDate);

            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String newFormattedBusinessDate = newDateFormat.format(date);
            log.info("Formatted Date {}", newFormattedBusinessDate);

            receiptServiceRequestDataDTO.setDateOfReceipt(newFormattedBusinessDate);

            log.info("create receipt LMS body {}", createReceiptBody);


            res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(createReceiptBody)
                    .typeResponseType(ServiceRequestSaveResponse.class)
                    .build().call();

            log.info("response create receipt {}", res);
            if (res.getData() != null) {
                if (res.getData().getServiceRequestId() == null) {
                    throw new Exception("1016035");
                }
                collectionActivityId = activityLogService.createActivityLogs(receiptServiceDtoRequest.getActivityData(), bearerToken);

                CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
                String updatedRemarks = CREATE_RECEIPT;
                updatedRemarks = updatedRemarks.replace("{receipt_number}", res.getData().getServiceRequestId().toString());
                updatedRemarks = updatedRemarks.replace("{loan_number}", receiptServiceDtoRequest.getRequestData().getLoanId());
                collectionActivityLogsEntity1.setRemarks(updatedRemarks);
                collectionActivityLogsRepository.save(collectionActivityLogsEntity1);

                CollectionReceiptEntity collectionReceiptEntity = new CollectionReceiptEntity();
                collectionReceiptEntity.setReceiptId(res.getData().getServiceRequestId());
                collectionReceiptEntity.setCreatedBy(receiptServiceDtoRequest.getActivityData().getUserId());
                collectionReceiptEntity.setReceiptHolderUserId(receiptServiceDtoRequest.getActivityData().getUserId());
                collectionReceiptEntity.setCollectionActivityLogsId(collectionActivityId);

                collectionReceiptRepository.save(collectionReceiptEntity);

                if (receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cash") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cheque") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi")) {
                    CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

                    log.info("collection limit user wise entity already exist {}", collectionLimitUser);


                    if (collectionLimitUser != null) {
                        collectionLimitUserWiseEntity.setCollectionLimitDefinitionsId(collectionLimitUser.getCollectionLimitDefinitionsId());
                        collectionLimitUserWiseEntity.setCreatedDate(new Date());
                        collectionLimitUserWiseEntity.setDeleted(collectionLimitUser.getDeleted());
                        collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUser.getCollectionLimitStrategiesKey());
                        collectionLimitUserWiseEntity.setUserId(collectionLimitUser.getUserId());
                        collectionLimitUserWiseEntity.setUserName(receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy());
                        collectionLimitUserWiseEntity.setTotalLimitValue(collectionLimitUser.getTotalLimitValue());
                        collectionLimitUserWiseEntity.setUtilizedLimitValue(collectionLimitUser.getUtilizedLimitValue() + Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()));

                    } else {
                        collectionLimitUserWiseEntity.setCreatedDate(new Date());
                        collectionLimitUserWiseEntity.setDeleted(false);
                        collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode());
                        collectionLimitUserWiseEntity.setUserId(receiptServiceDtoRequest.getActivityData().getUserId());
                        collectionLimitUserWiseEntity.setUserName(receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy());
                        collectionLimitUserWiseEntity.setTotalLimitValue(currentReceiptAmountAllowed);
                        collectionLimitUserWiseEntity.setUtilizedLimitValue(Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()));
                    }
                    log.info("collection limit user wise entity {}", collectionLimitUserWiseEntity);
                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
                }
            } else {
                return res;
            }




        } catch (Exception ee) {
//            if (collectionActivityId != null) {
//                collectionActivityLogsRepository.deleteById(collectionActivityId);
//            }
            throw new Exception(ee.getMessage());
        }
        return res;
    }


    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception {
        List<LoanAllocationEntity> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getLoansByAllocatedToUserId(allocatedToUserId);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return loanAllocationEntities;
    }


    public Object getReceiptDate(String bearerToken) throws Exception {
        ReceiptServiceSystemPropertiesResponse lmsBusinessDate = new ReceiptServiceSystemPropertiesResponse();
//        ReceiptServiceDtoRequest createReceiptBody = new ObjectMapper().convertValue(receiptServiceDtoRequest, ReceiptServiceDtoRequest.class);

        GetReceiptDateResponse getReceiptDateResponse = new GetReceiptDateResponse();
        ReceiptDateResponse receiptDateResponse = new ReceiptDateResponse();
        log.info("get receipt date {}", receiptDateResponse);
        BaseDTOResponse<Object> baseResponse = null;
        try {

            String businessDateConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(USE_BUSINESS_DATE_AS_RECEIPT_DATE);
            String transactionDateConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(USE_BUSINESS_DATE_AS_TRANSACTION_DATE);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");

            lmsBusinessDate = HTTPRequestService.<Object, ReceiptServiceSystemPropertiesResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getSystemProperties")
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ReceiptServiceSystemPropertiesResponse.class)
                    .build().call();

            if (businessDateConf.equals("true")) {
                String bDate = lmsBusinessDate.data.businessDate;
                SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormatter.parse(bDate);
                String busDate = outputFormatter.format(date);
                getReceiptDateResponse.setBusinessDate(busDate);
            } else {
                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String cDate = dateObj.format(formatter);
                getReceiptDateResponse.setBusinessDate(cDate);
            }
            baseResponse = new BaseDTOResponse<Object>(getReceiptDateResponse);

            if (transactionDateConf.equals("true")) {
                String bDate = lmsBusinessDate.data.businessDate;
                SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormatter.parse(bDate);
                String busDate = outputFormatter.format(date);
                getReceiptDateResponse.setTransactionDate(busDate);
            } else {
                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String cDate = dateObj.format(formatter);
                getReceiptDateResponse.setTransactionDate(cDate);
            }
            baseResponse = new BaseDTOResponse<Object>(getReceiptDateResponse);
            log.info("Receipt Date {}", baseResponse);
        } catch (Exception ee) {
            throw new Exception(ee);
        }
        return baseResponse;
    }


    public void getPdf(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
//        log.info("value is called -- {} ", serviceRequestId);
        String serviceRequestId = null;
        String deliverableType = null;

        if (httpServletRequest.getParameter("deliverableType") != null) {
            deliverableType = httpServletRequest.getParameter("deliverableType");
        }
        if (httpServletRequest.getParameter("serviceRequestId") != null) {
            serviceRequestId = httpServletRequest.getParameter("serviceRequestId");
        }

        String url = "http://localhost:1102/v1/getPdf?deliverableType=" + deliverableType + "&serviceRequestId=" + serviceRequestId;

        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setBearerAuth("6c6e74fe-7dc4-4a8f-891e-fbe4d061cafc");
            httpHeaders.setBearerAuth(token);

        ResponseEntity<byte[]> response;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                byte[].class);

        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(Objects.requireNonNull(response.getBody()));
        outputStream.close();

    }
}
