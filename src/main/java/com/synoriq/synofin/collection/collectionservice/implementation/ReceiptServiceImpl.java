package com.synoriq.synofin.collection.collectionservice.implementation;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.exception.DataLockException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferLmsFilterDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.SystemPropertiesDTOs.GetReceiptDateResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.SystemPropertiesDTOs.ReceiptServiceSystemPropertiesResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.FinovaSmsService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.CREATE_RECEIPT;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    private RSAUtils rsaUtils;
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
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private ReceiptTransferService receiptTransferService;

    @Autowired
    private RegisteredDeviceInfoRepository registeredDeviceInfoRepository;

    @Autowired
    ReceiptTransferHistoryRepository receiptTransferHistoryRepository;

    @Autowired
    private IntegrationConnectorService integrationConnectorService;

    private final Map<String, ReentrantLock> lockMap = new HashMap<>();


    @Override
    public BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String searchKey, Integer page, Integer size) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
            List<Map<String, Object>> taskDetailPages;
            Pageable pageRequest;
//            if (page > 0) {
//                page = page - 1;
//            }
            pageRequest = PageRequest.of(page, size);
            if (!Objects.equals(searchKey, "")) {
                taskDetailPages = receiptRepository.getReceiptsBySearchKey(userName, searchKey, encryptionKey, password, piiPermission, pageRequest);
            } else {
                taskDetailPages = receiptRepository.getReceiptsByUserIdWithDuration(userName, fromDate, toDate, encryptionKey, password, piiPermission, pageRequest);
            }

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
            List<Map<String, Object>> receiptsData = receiptRepository.getReceiptsByUserIdWhichNotTransferred(userName, encryptionKey, password, piiPermission);

            Set<Long> receiptIds = receiptsData.stream()
                    .map(data -> Long.parseLong(data.get("id").toString()))
                    .collect(Collectors.toSet());

            Set<Long> transferredReceiptIds = receiptTransferHistoryRepository.findByDeletedAndCollectionReceiptsIdIn(false, receiptIds)
                    .stream()
                    .map(ReceiptTransferHistoryEntity::getCollectionReceiptsId)
                    .collect(Collectors.toSet());

            receiptIds.removeAll(transferredReceiptIds);

            List<Map<String, Object>> ans = receiptsData.stream()
                    .filter(data -> receiptIds.contains(Long.parseLong(data.get("id").toString())))
                    .collect(Collectors.toList());

            baseDTOResponse = new BaseDTOResponse<>(ans);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByLoanIdWithDuration(loanId, fromDate, toDate);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    @Override
    @Transactional
    public ServiceRequestSaveResponse createReceipt(@RequestBody ReceiptServiceDtoRequest receiptServiceDtoRequest, String bearerToken, boolean receiptFromQR) throws Exception {
        ServiceRequestSaveResponse res;
        Long collectionActivityId;
        ReceiptServiceDtoRequest createReceiptBody = new ObjectMapper().convertValue(receiptServiceDtoRequest, ReceiptServiceDtoRequest.class);

        ReceiptServiceRequestDataDTO receiptServiceRequestDataDTO = new ReceiptServiceRequestDataDTO();


        boolean lockAcquired = false;
// Acquire a lock for the customer record
        ReentrantLock lock = null;
        String lockId = null;
        if (receiptServiceDtoRequest.getLoanApplicationNumber() != null) {
            lockId = receiptServiceDtoRequest.getLoanApplicationNumber();
            log.info("lockId -> {}", lockId);
            lock = lockMap.computeIfAbsent(lockId, id -> new ReentrantLock());
            if (lock.isLocked()) {
                throw new DataLockException("Record is already locked for modification");
            }
            lock.lock();
            lockAcquired = true;
        }

        try {

//            String employeeMobileNumberValidation = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(EMPLOYEE_MOBILE_NUMBER_VALIDATION);
//            if(!employeeMobileNumberValidation.equals("true")) {
//                String employeeMobileNumber = registeredDeviceInfoRepository.getEmployeeMobileNumber(receiptServiceDtoRequest.getCollectedFromNumber());
//                if(employeeMobileNumber != null) {
//                    throw new Exception("1016047");
//                }
//            }

            // always in minutes
            long validationTime = Long.parseLong(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RECEIPT_TIME_VALIDATE));

            // check for duplicate receipt generate under 10 min
            Map<String, Object> createReceiptTimeError = receiptRepository.getReceiptData(Long.parseLong(createReceiptBody.getRequestData().getLoanId()), createReceiptBody.getRequestData().getRequestData().getReceiptAmount());
            if (!createReceiptTimeError.isEmpty()) {
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
            if (!receiptFromQR && receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi")) {
                Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(receiptServiceDtoRequest.getRequestData().getRequestData().getTransactionReference());
                if (!transactionNumberCheck.isEmpty()) {
                    throw new Exception("1016039");
                }
            }

            String limitConf = null;
            switch (receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode()) {
                case "cash":
                    limitConf = CASH_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "cheque":
                    limitConf = CHEQUE_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "upi":
                    limitConf = ONLINE_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "neft":
                    limitConf = NEFT_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "rtgs":
                    limitConf = RTGS_COLLECTION_DEFAULT_LIMIT;
                    break;
            }


            Double totalLimitValue;
            double currentReceiptAmountAllowed;
            double receiptAmount = Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());
            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode);

            if (collectionLimitUser != null) {
                totalLimitValue = collectionLimitUser.getTotalLimitValue();
                currentReceiptAmountAllowed = totalLimitValue - collectionLimitUser.getUtilizedLimitValue();
                log.info("Utilized limit {}", collectionLimitUser.getUtilizedLimitValue());
            } else {
                // Initializing currentReceiptAmountAllowed with same receipt amount in case of payment mode configuration not found ( NEFT & RTGS ).
                currentReceiptAmountAllowed = limitConf != null ? Double.parseDouble(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf)) : Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()) + 1.0;
            }

            // per day cash limit check
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash")) {
                double perDayCashLimitLoan = Double.parseDouble(collectionConfigurationsRepository.findConfigurationValueByConfigurationName("per_day_cash_collection_customer_limit"));
                double receiptCollectedAmountTillToday = receiptRepository.getCollectedAmountToday(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()));

//                log.info("perDayCashLimitLoan {}", perDayCashLimitLoan);
//                log.info("receiptCollectedAmountTillToday {}", receiptCollectedAmountTillToday);
//                log.info("receiptAmount {}", receiptAmount);
                if (receiptCollectedAmountTillToday + receiptAmount > perDayCashLimitLoan) {
                    throw new Exception("1017005");
                }
            }

            // per month cash limit check
            String monthLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MONTH_CASH_VALIDATION);
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash") && !monthLimit.equals("false")) {
                Date beginning, end;

                {
                    Calendar calendar = getCalendarForNow();
                    calendar.set(Calendar.DAY_OF_MONTH,
                            calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    setTimeToBeginningOfDay(calendar);
                    beginning = calendar.getTime();
                }

                {
                    Calendar calendar = getCalendarForNow();
                    calendar.set(Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    setTimeToEndofDay(calendar);
                    end = calendar.getTime();
                }
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                log.info("begining {}", beginning);
                log.info(" end {}", end);
                String fromDate = dateFormat.format(beginning);
                String toDate = dateFormat.format(end);

                double perMonthCashLimitLoan = Double.parseDouble(monthLimit);
                double receiptCollectedAmountWithinMonth = receiptRepository.getCollectedAmountWithinMonth(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()), fromDate, toDate);

                if (receiptCollectedAmountWithinMonth + receiptAmount > perMonthCashLimitLoan) {
                    throw new Exception("1016043");
                }
            }
//            log.info("Total Limit Value {}", totalLimitValue);
//            log.info("Receipt amount can be collected by a user at current situation {}", currentReceiptAmountAllowed);
//            log.info("Receipt amount {}", receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());

            if (currentReceiptAmountAllowed < Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount())) {
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

//            log.info("create receipt LMS body {}", createReceiptBody);


            res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(createReceiptBody)
                    .typeResponseType(ServiceRequestSaveResponse.class)
                    .build().call();

            log.info("response create receipt {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, createReceiptBody.getActivityData().getUserId(), createReceiptBody, res, "success", createReceiptBody.getActivityData().getLoanId(), HttpMethod.POST.name(), "createReceipt");
            if (res.getData() != null) {
                if (res.getData().getServiceRequestId() == null) {
                    return res;
//                    res.getError().getText();
//                    throw new Exception("1016035");
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

                if (receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cash") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("cheque") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("neft") || receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("rtgs")) {
                    CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

//                    log.info("collection limit user wise entity already exist {}", collectionLimitUser);


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
//                    log.info("collection limit user wise entity {}", collectionLimitUserWiseEntity);
                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
                }
            } else {
                return res;
            }


        } catch (Exception ee) {
//            if (collectionActivityId != null) {
//                collectionActivityLogsRepository.deleteById(collectionActivityId);
//            }
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, createReceiptBody.getActivityData().getUserId(), createReceiptBody, modifiedErrorMessage, "failure", createReceiptBody.getActivityData().getLoanId(), HttpMethod.POST.name(), "createReceipt");
            throw new Exception(ee.getMessage());
        } finally {
            // Release the lock
            if (lock != null && lockAcquired) {
                log.info("lock release for id {}", lockId);
                // Release the lock
                lock.unlock();
            }
        }
        return res;
    }
    @Override
    @Transactional
    public ServiceRequestSaveResponse createReceiptNew(Object object, MultipartFile paymentReferenceImage, MultipartFile selfieImage, String bearerToken, boolean receiptFromQR) throws Exception {
        log.info("createReceiptNew Begin");
        ServiceRequestSaveResponse res;
        Long collectionActivityId;
        ReceiptServiceDtoRequest receiptServiceDtoRequest;
        ObjectMapper objectMapper = new ObjectMapper();
        if (object instanceof ReceiptServiceDtoRequest) {
            receiptServiceDtoRequest = (ReceiptServiceDtoRequest) object;
        } else {
            JsonNode jsonNode = objectMapper.readTree(String.valueOf(object));
            receiptServiceDtoRequest = objectMapper.convertValue(jsonNode, ReceiptServiceDtoRequest.class);
        }

        ReceiptServiceRequestDataDTO receiptServiceRequestDataDTO = new ReceiptServiceRequestDataDTO();

        GeoLocationDTO geoLocationDTO = objectMapper.convertValue(receiptServiceDtoRequest.getActivityData().getGeolocationData(), GeoLocationDTO.class);

        boolean lockAcquired = false;
        // Acquire a lock for the customer record
        ReentrantLock lock = null;
        String lockId = null;
        if (receiptServiceDtoRequest.getLoanApplicationNumber() != null) {
            lockId = receiptServiceDtoRequest.getLoanApplicationNumber();
            log.info("lockId -> {}", lockId);
            lock = lockMap.computeIfAbsent(lockId, id -> new ReentrantLock());
            if (lock.isLocked()) {
                throw new DataLockException("Record is already locked for modification");
            }
            lock.lock();
            lockAcquired = true;
        }
        log.info("Start time : {}", new Date().getTime());
        try {
            // multithreading
            List<MultipartFile> allImages = new LinkedList<>();
            allImages.add(paymentReferenceImage);
            allImages.add(selfieImage);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor = new DelegatingSecurityContextExecutorService(executor, SecurityContextHolder.getContext());
            List<Future<UploadImageOnS3ResponseDTO>> allResults = new LinkedList<>();

            if (!receiptFromQR) {  // create receipt hitting from  callback
                for (MultipartFile image : allImages) {
                    allResults.add(executor.submit(() -> integrationConnectorService.uploadImageOnS3(bearerToken, image, "create_receipt", geoLocationDTO, receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
                }
                executor.shutdown();
                if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    throw new Exception("ExecutorService did not terminate in the specified time.");
                }
                // Wait for both image uploads to complete
                UploadImageOnS3ResponseDTO paymentReference = allResults.get(0).get();
                UploadImageOnS3ResponseDTO selfie = allResults.get(1).get();

                Map<String, Object> imageMap = getStringObjectMap(paymentReference, selfie);
                receiptServiceDtoRequest.getActivityData().setImages(imageMap);
            }
            log.info("End time : {}", new Date().getTime());

            // always in minutes
            long validationTime = Long.parseLong(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RECEIPT_TIME_VALIDATE));

            // check for duplicate receipt generate under 10 min
            Map<String, Object> createReceiptTimeError = receiptRepository.getReceiptData(Long.parseLong(receiptServiceDtoRequest.getRequestData().getLoanId()), receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());
            if (!createReceiptTimeError.isEmpty()) {
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
            if (!receiptFromQR && receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode().equals("upi")) {
                Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(receiptServiceDtoRequest.getRequestData().getRequestData().getTransactionReference());
                if (!transactionNumberCheck.isEmpty()) {
                    throw new Exception("1016039");
                }
            }

            String limitConf = null;
            switch (receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode()) {
                case "cash":
                    limitConf = CASH_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "cheque":
                    limitConf = CHEQUE_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "upi":
                    limitConf = ONLINE_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "neft":
                    limitConf = NEFT_COLLECTION_DEFAULT_LIMIT;
                    break;
                case "rtgs":
                    limitConf = RTGS_COLLECTION_DEFAULT_LIMIT;
                    break;
            }


            double receiptAmount = Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount());
            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode);

            double currentReceiptAmountAllowed;
            if (collectionLimitUser != null) {
                Double totalLimitValue = collectionLimitUser.getTotalLimitValue();
                currentReceiptAmountAllowed = totalLimitValue - collectionLimitUser.getUtilizedLimitValue();
                log.info("Utilized limit {}", collectionLimitUser.getUtilizedLimitValue());
            } else {
                // Initializing currentReceiptAmountAllowed with same receipt amount in case of payment mode configuration not found ( NEFT & RTGS ).
                currentReceiptAmountAllowed = limitConf != null ? Double.parseDouble(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf)) : Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()) + 1.0;
            }

            // per day cash limit check
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash")) {
                double perDayCashLimitLoan = Double.parseDouble(collectionConfigurationsRepository.findConfigurationValueByConfigurationName("per_day_cash_collection_customer_limit"));
                double receiptCollectedAmountTillToday = receiptRepository.getCollectedAmountToday(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()));

                if (receiptCollectedAmountTillToday + receiptAmount > perDayCashLimitLoan) {
                    throw new Exception("1017005");
                }
            }

            // per month cash limit check
            String monthLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MONTH_CASH_VALIDATION);
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash") && !monthLimit.equals("false")) {
                Date beginning, end;

                {
                    Calendar calendar = getCalendarForNow();
                    calendar.set(Calendar.DAY_OF_MONTH,
                            calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    setTimeToBeginningOfDay(calendar);
                    beginning = calendar.getTime();
                }

                {
                    Calendar calendar = getCalendarForNow();
                    calendar.set(Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    setTimeToEndofDay(calendar);
                    end = calendar.getTime();
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                log.info("begining {}", beginning);
                log.info(" end {}", end);
                String fromDate = dateFormat.format(beginning);
                String toDate = dateFormat.format(end);

                double perMonthCashLimitLoan = Double.parseDouble(monthLimit);
                double receiptCollectedAmountWithinMonth = receiptRepository.getCollectedAmountWithinMonth(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()), fromDate, toDate);

                if (receiptCollectedAmountWithinMonth + receiptAmount > perMonthCashLimitLoan) {
                    throw new Exception("1016043");
                }
            }

            if (currentReceiptAmountAllowed < Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount())) {
                throw new Exception("1017003");
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");


            String bDate = receiptServiceDtoRequest.getRequestData().getRequestData().getDateOfReceipt();

            receiptServiceRequestDataDTO.setDateOfReceipt(formatDate(bDate));


            res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(receiptServiceDtoRequest)
                    .typeResponseType(ServiceRequestSaveResponse.class)
                    .build().call();

            log.info("response create receipt {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, res, "success", receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), "createReceipt");
            if (res.getData() != null) {
                if (res.getData().getServiceRequestId() == null) {
                    log.info("Receipt Error {}", res.getError().getText());
                    return res;
                }

                String updatedRemarks = CREATE_RECEIPT;
                updatedRemarks = updatedRemarks.replace("{receipt_number}", res.getData().getServiceRequestId().toString());
                updatedRemarks = updatedRemarks.replace("{loan_number}", receiptServiceDtoRequest.getRequestData().getLoanId());
                receiptServiceDtoRequest.getActivityData().setRemarks(updatedRemarks);
                collectionActivityId = activityLogService.createActivityLogs(receiptServiceDtoRequest.getActivityData(), bearerToken);

                Map<String, Object> collectionReceiptMap = new HashMap<>();
                collectionReceiptMap.put("service_request_id", res.getData().getServiceRequestId());
                collectionReceiptMap.put("activity_id", collectionActivityId);
                collectionReceiptMap.put("user_id", receiptServiceDtoRequest.getActivityData().getUserId());

                createCollectionReceipt(collectionReceiptMap, bearerToken);


                // setting collection limit userwise and create collection receipt
                createCollectionReceipt(collectionReceiptMap, bearerToken);
                setCollectionLimitUserWiseEntity(collectionLimitUser, receiptServiceDtoRequest, currentReceiptAmountAllowed);


                // multi receipt for particular clients
                String multiReceiptClientCredentials = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MULTI_RECEIPT_CLIENT_CREDENTIALS);
                if (!multiReceiptClientCredentials.equals("false")) {
                    ArrayList<Map<String, Object>> list = new ObjectMapper().readValue(multiReceiptClientCredentials, new TypeReference<ArrayList<Map<String, Object>>>() { });
                    ExecutorService executor2 = Executors.newFixedThreadPool(2);
                    for (Map<String, Object> map : list) {
                        String token = utilityService.getTokenByApiKeySecret(map);

                        executor2 = new DelegatingSecurityContextExecutorService(executor2, SecurityContextHolder.getContext());
                        allResults = new LinkedList<>();
                        for (MultipartFile image : allImages) {
                            allResults.add(executor2.submit(() -> integrationConnectorService.uploadImageOnS3(token, image, "create_receipt", geoLocationDTO, receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
                        }
                        executor2.shutdown();
                        if (!executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                            throw new Exception("ExecutorService did not terminate in the specified time.");
                        }

                        // Wait for both image uploads to complete
                        UploadImageOnS3ResponseDTO paymentReference = allResults.get(0).get();
                        UploadImageOnS3ResponseDTO selfie = allResults.get(1).get();

                        Map<String, Object> imageMapMulti = getStringObjectMap(paymentReference, selfie);
                        receiptServiceDtoRequest.getActivityData().setImages(imageMapMulti);
                        ServiceRequestSaveResponse multiReceiptResponse = multiReceiptAfterReceipt(receiptServiceDtoRequest, token);

                        updatedRemarks = CREATE_RECEIPT;
                        updatedRemarks = updatedRemarks.replace("{receipt_number}", multiReceiptResponse.getData().getServiceRequestId().toString());
                        updatedRemarks = updatedRemarks.replace("{loan_number}", receiptServiceDtoRequest.getRequestData().getLoanId());
                        receiptServiceDtoRequest.getActivityData().setRemarks(updatedRemarks);

                        // calling activity logs API for lifpl client
                        String url = "http://localhost:1101/v1/";

                        HttpHeaders httpHeader = new HttpHeaders();
                        httpHeader.add("Content-Type", "application/json");
                        httpHeader.setBearerAuth(token);

                        ResponseEntity<Object> activityResponse = new RestTemplate().exchange(
                                url + "activity-logs",
                                HttpMethod.POST,
                                new HttpEntity<>(receiptServiceDtoRequest.getActivityData(), httpHeader),
                                Object.class
                        );
                        log.info("here^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                        Map<String, Object> baseResponse = objectMapper.convertValue(activityResponse.getBody(), Map.class);
                        collectionActivityId = Long.parseLong(baseResponse.get("data").toString());
                        log.info("**********************************************reached");

                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("service_request_id", multiReceiptResponse.getData().getServiceRequestId());
                        hashMap.put("user_id", receiptServiceDtoRequest.getActivityData().getUserId());
                        hashMap.put("activity_id", collectionActivityId);
                        log.info("hashMap {}", hashMap);
                        new RestTemplate().exchange(
                                url + "create-collection-receipt",
                                HttpMethod.POST,
                                new HttpEntity<>(hashMap, httpHeader),
                                Object.class
                        );
                    }
                }
            } else {
                return res;
            }


        } catch (Exception ee) {
            log.error("error occurred", ee);
            log.error("error message {}", ee.getMessage());
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, modifiedErrorMessage, "failure", receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), "createReceipt");
            throw new Exception(ee.getMessage());
        } finally {
            // Release the lock
            if (lock != null && lockAcquired) {
                log.info("lock release for id {}", lockId);
                // Release the lock
                lock.unlock();
            }
        }
        log.info("createReceiptNew End");
        return res;
    }
    private static String formatDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date formattedDate = sdf.parse(date);
        return new SimpleDateFormat("yyyy-MM-dd").format(formattedDate);
    }

    @NotNull
    private CollectionLimitUserWiseEntity setCollectionLimitUserWiseEntity(CollectionLimitUserWiseEntity collectionLimitUser, ReceiptServiceDtoRequest receiptServiceDtoRequest, double currentReceiptAmountAllowed) {
        CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
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
        collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
        return collectionLimitUserWiseEntity;
    }

    @NotNull
    private static Map<String, Object> getStringObjectMap(UploadImageOnS3ResponseDTO paymentReference, UploadImageOnS3ResponseDTO selfie) {
        String url1 = paymentReference.getData() != null ? paymentReference.getData().getFileName() : null;
        String url2 = selfie.getData() != null ? selfie.getData().getFileName() : null;

        // creating images Object
        Map<String, Object> imageMap = new HashMap<>();
        int i = 1;
        if (url1 != null) {
            imageMap.put("url" + i, url1);
            i++;
        }
        if (url2 != null) {
            imageMap.put("url" + i, url2);
        }
        return imageMap;
    }

    private ServiceRequestSaveResponse multiReceiptAfterReceipt(ReceiptServiceDtoRequest receiptServiceDtoRequest, String token) throws Exception {
        log.info("Begin multiReceiptAfterReceipt");


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.add("Content-Type", "application/json");

        ServiceRequestSaveResponse res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url("http://localhost:1102/v1/createReceipt")
                .httpHeaders(httpHeaders)
                .body(receiptServiceDtoRequest)
                .typeResponseType(ServiceRequestSaveResponse.class)
                .build().call();

        log.info("multiReceiptAfterReceipt Response {}", res);
        log.info("End multiReceiptAfterReceipt");

        // creating consumed log api
        consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.multi_create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, res, "success", receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), "createReceipt");
        return res;
    }

    @Override
    public Object getReceiptDate(String bearerToken) throws Exception {
        ReceiptServiceSystemPropertiesResponse lmsBusinessDate;

        GetReceiptDateResponse getReceiptDateResponse = new GetReceiptDateResponse();
//        log.info("get receipt date {}", receiptDateResponse);
        BaseDTOResponse<Object> baseResponse;
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
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_receipt_date, null, null, lmsBusinessDate, "success", null, HttpMethod.POST.name(), "getReceiptDate");

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
            baseResponse = new BaseDTOResponse<>(getReceiptDateResponse);
//            log.info("Receipt Date {}", baseResponse);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_receipt_date, null, null, modifiedErrorMessage, "failure", null, HttpMethod.POST.name(), "getReceiptDate");
            throw new Exception(ee);
        }
        return baseResponse;
    }

    @Override
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

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    @Override
    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferredForPortal(ReceiptTransferLmsFilterDTO filterDTO) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Pageable pageRequest;
            pageRequest = PageRequest.of(filterDTO.getPage(), filterDTO.getSize());
            Boolean piiPermission = true;
            Map<String, Object> mainData = new HashMap<>();
            if (filterDTO.getIsFilter().equals(true)) {
                return receiptTransferService.getReceiptTransferByFilter(filterDTO);
            } else {
                List<Map<String, Object>> receiptsData = receiptRepository.getReceiptsByUserIdWhichNotTransferredForPortal(filterDTO.getPaymentMode(), encryptionKey, password, piiPermission, pageRequest);
                if (!receiptsData.isEmpty()) {
                    mainData.put("receipts_data", receiptsData);
                    mainData.put("total_rows", receiptsData.get(0).get("total_rows"));
                } else {
                    mainData.put("receipts_data", new ArrayList<>());
                    mainData.put("total_rows", 0);
                }
            }
            baseDTOResponse = new BaseDTOResponse<>(mainData);
        } catch (Exception e) {
            throw new Exception("1017000");
        }

        return baseDTOResponse;

    }

    @Override
    public String createCollectionReceipt(Map<String, Object> requestBody, String token) throws Exception {

        CollectionReceiptEntity collectionReceiptEntity = new CollectionReceiptEntity();
        collectionReceiptEntity.setReceiptId(Long.parseLong(requestBody.get("service_request_id").toString()));
        collectionReceiptEntity.setCreatedBy(Long.parseLong(requestBody.get("user_id").toString()));
        collectionReceiptEntity.setReceiptHolderUserId(Long.parseLong(requestBody.get("user_id").toString()));
        collectionReceiptEntity.setCollectionActivityLogsId(Long.parseLong(requestBody.get("activity_id").toString()));

        collectionReceiptRepository.save(collectionReceiptEntity);
        return "data_saved_successfully";
    }
}
