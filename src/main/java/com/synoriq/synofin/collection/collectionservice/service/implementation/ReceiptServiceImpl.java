package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.common.exception.DataLockException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferLmsFilterDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.systempropertiesdtos.GetReceiptDateResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.systempropertiesdtos.ReceiptDateResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.FinovaSmsService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.CREATE_RECEIPT;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private static final String STATUS_MESSAGE = "success";
    private static final String CONTENT_KEY = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";
    private static final String[] COLLECTION_RECIPES_KEY = {"activity_id", "user_id"};
    private static final String[] DATE_FORMATS = {"yyyy-MM-dd", ""};
    private static final String FAILURE_STATUS = "failure";
    private static final String CREATE_RECEIPT_STATUS = "createReceipt";
    private static final String TOTAL_ROWS_STR = "total_rows";
    private static final String EXCEP_CODE = "1017002";


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
    @Autowired
    private RestTemplate restTemplate;

    private final Map<String, ReentrantLock> lockMap = new HashMap<>();

    private static final String SERVICE_REQ_ID = "service_request_id";


    @Override
    public BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String searchKey, Integer page, Integer size) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;
            List<Map<String, Object>> taskDetailPages;
            Pageable pageRequest;

            pageRequest = PageRequest.of(page, size);
            if (!Objects.equals(searchKey, "")) {
                taskDetailPages = receiptRepository.getReceiptsBySearchKey(userName, searchKey, encryptionKey, password, piiPermission, pageRequest);
            } else {
                taskDetailPages = receiptRepository.getReceiptsByUserIdWithDuration(userName, fromDate, toDate, encryptionKey, password, piiPermission, pageRequest);
            }

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
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
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByLoanIdWithDuration(loanId, fromDate, toDate);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }
    @Override
    @Transactional
    public ServiceRequestSaveResponse createReceiptNew(Object object, MultipartFile paymentReferenceImage, MultipartFile selfieImage, String bearerToken, boolean receiptFromQR) throws CustomException, InterruptedException, ConnectorException, JsonProcessingException, DataLockException {
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
        // Acquire a lock for the loan wise
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

            if (!receiptFromQR) {  // create receipt hitting from  callback
                for (MultipartFile image : allImages) {
                    allResults.add(executor.submit(() -> integrationConnectorService.uploadImageOnS3(bearerToken, image, "create_receipt", geoLocationDTO, receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
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
                    ErrorCode errCode = ErrorCode.getErrorCode(1016038);
                    throw new CollectionException(errCode, 1016038);
                }
            }
            // check for duplicate transaction reference number
            if (!receiptServiceDtoRequest.getRequestData().getRequestData().getTransactionReference().isBlank()) {
                Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(receiptServiceDtoRequest.getRequestData().getRequestData().getTransactionReference());
                if (!transactionNumberCheck.isEmpty()) {
                    ErrorCode errCode = ErrorCode.getErrorCode(1016039);
                    throw new CollectionException(errCode, 1016039);
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
                default:
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
                    ErrorCode errCode = ErrorCode.getErrorCode(1017005);
                    throw new CollectionException(errCode, 1017005);
                }
            }

            // per month cash limit check
            String monthLimit = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MONTH_CASH_VALIDATION);
            if (receiptServiceDtoRequest.getRequestData().getRequestData().paymentMode.equals("cash") && !monthLimit.equals("false")) {
                Date beginning;
                Date end;

                beginning = this.getBeginningDate();
                end = this.getEndDate();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                log.info("begining {}", beginning);
                log.info(" end {}", end);
                String fromDate = dateFormat.format(beginning);
                String toDate = dateFormat.format(end);

                double perMonthCashLimitLoan = Double.parseDouble(monthLimit);
                double receiptCollectedAmountWithinMonth = receiptRepository.getCollectedAmountWithinMonth(Long.valueOf(receiptServiceDtoRequest.getRequestData().getLoanId()), fromDate, toDate);

                if (receiptCollectedAmountWithinMonth + receiptAmount > perMonthCashLimitLoan) {
                    ErrorCode errCode = ErrorCode.getErrorCode(1016043);
                    throw new CollectionException(errCode, 1016043);
                }
            }

            if (currentReceiptAmountAllowed < Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount())) {
                ErrorCode errCode = ErrorCode.getErrorCode(1017003);
                throw new CollectionException(errCode, 1017003);
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add(CONTENT_KEY, CONTENT_TYPE);


            String bDate = receiptServiceDtoRequest.getRequestData().getRequestData().getDateOfReceipt();

            receiptServiceRequestDataDTO.setDateOfReceipt(formatDate(bDate));


            res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(receiptServiceDtoRequest)
                    .typeResponseType(ServiceRequestSaveResponse.class)
                    .build().call(restTemplate);

            log.info("response create receipt {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, res, STATUS_MESSAGE, receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), CREATE_RECEIPT_STATUS);
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
                collectionReceiptMap.put(SERVICE_REQ_ID, res.getData().getServiceRequestId());
                collectionReceiptMap.put(COLLECTION_RECIPES_KEY[0], collectionActivityId);
                collectionReceiptMap.put(COLLECTION_RECIPES_KEY[1], receiptServiceDtoRequest.getActivityData().getUserId());



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
                        allResults = new LinkedList<>();
                        executor2 = new DelegatingSecurityContextExecutorService(executor2, SecurityContextHolder.getContext());
                        for (MultipartFile image : allImages) {
                            allResults.add(executor2.submit(() -> integrationConnectorService.uploadImageOnS3(token, image, "create_receipt", geoLocationDTO, receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy())));
                        }
                        executor2.shutdown();
                        if (!executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
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
                        ServiceRequestSaveResponse multiReceiptResponse = multiReceiptAfterReceipt(receiptServiceDtoRequest, token);

                        updatedRemarks = CREATE_RECEIPT;
                        updatedRemarks = updatedRemarks.replace("{receipt_number}", multiReceiptResponse.getData().getServiceRequestId().toString());
                        updatedRemarks = updatedRemarks.replace("{loan_number}", receiptServiceDtoRequest.getRequestData().getLoanId());
                        receiptServiceDtoRequest.getActivityData().setRemarks(updatedRemarks);

                        // calling activity logs API for lifpl client
                        String url = "http://localhost:1101/v1/";

                        HttpHeaders httpHeader = new HttpHeaders();
                        httpHeader.add(CONTENT_KEY, CONTENT_TYPE);
                        httpHeader.setBearerAuth(token);

                        ResponseEntity<Object> activityResponse = restTemplate.exchange(
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
                        hashMap.put(SERVICE_REQ_ID, multiReceiptResponse.getData().getServiceRequestId());
                        hashMap.put(COLLECTION_RECIPES_KEY[1], receiptServiceDtoRequest.getActivityData().getUserId());
                        hashMap.put(COLLECTION_RECIPES_KEY[0], collectionActivityId);
                        log.info("hashMap {}", hashMap);
                        restTemplate.exchange(
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

        } catch (ConnectorException ee) {
            String modifiedErrorMessage = utilityService.convertToJSON(ee.getMessage());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, modifiedErrorMessage, FAILURE_STATUS, receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), CREATE_RECEIPT_STATUS);
            throw new ConnectorException(ErrorCode.S3_UPLOAD_DATA_ERROR, ee.getText(), HttpStatus.FAILED_DEPENDENCY, ee.getRequestId());
        } catch (InterruptedException ee) {
            log.error("Interrupted Exception Error {}", ee.getMessage());
            Thread.currentThread().interrupt();
            throw new InterruptedException(ee.getMessage());
        } catch (Exception ee) {
            log.error("error occurred {} -> message -> {}", ee, ee.getMessage());
            String modifiedErrorMessage = utilityService.convertToJSON(ee.getMessage());
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, modifiedErrorMessage, FAILURE_STATUS, receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), CREATE_RECEIPT_STATUS);
            if (ee.getMessage().contains("Custom")) {
                CustomException customException = (CustomException) ee.getCause();
                throw new CustomException(customException.getText());
            }
            throw new CustomException(ee.getMessage());
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
        return new SimpleDateFormat(DATE_FORMATS[0]).format(formattedDate);
    }


    private CollectionLimitUserWiseEntity setCollectionLimitUserWiseEntity(CollectionLimitUserWiseEntity collectionLimitUser, ReceiptServiceDtoRequest receiptServiceDtoRequest, double currentReceiptAmountAllowed) {
        CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
        if (collectionLimitUser != null) {
            collectionLimitUserWiseEntity.setCreatedDate(new Date());
            collectionLimitUserWiseEntity.setCollectionLimitDefinitionsId(collectionLimitUser.getCollectionLimitDefinitionsId());
            collectionLimitUserWiseEntity.setDeleted(collectionLimitUser.getDeleted());
            collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUser.getCollectionLimitStrategiesKey());
            collectionLimitUserWiseEntity.setUserId(collectionLimitUser.getUserId());
            collectionLimitUserWiseEntity.setUserName(receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy());
            collectionLimitUserWiseEntity.setName(collectionLimitUserWiseRepository.getNameByUserId(collectionLimitUser.getUserId()));
            collectionLimitUserWiseEntity.setTotalLimitValue(collectionLimitUser.getTotalLimitValue());
            collectionLimitUserWiseEntity.setUtilizedLimitValue(collectionLimitUser.getUtilizedLimitValue() + Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()));

        } else {
            collectionLimitUserWiseEntity.setCreatedDate(new Date());
            collectionLimitUserWiseEntity.setDeleted(false);
            collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode());
            collectionLimitUserWiseEntity.setUserId(receiptServiceDtoRequest.getActivityData().getUserId());
            collectionLimitUserWiseEntity.setUserName(receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy());
            collectionLimitUserWiseEntity.setName(collectionLimitUserWiseRepository.getNameByUserId(receiptServiceDtoRequest.getActivityData().getUserId()));
            collectionLimitUserWiseEntity.setTotalLimitValue(currentReceiptAmountAllowed);
            collectionLimitUserWiseEntity.setUtilizedLimitValue(Double.parseDouble(receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount()));
        }
        collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
        return collectionLimitUserWiseEntity;
    }



    private ServiceRequestSaveResponse multiReceiptAfterReceipt(ReceiptServiceDtoRequest receiptServiceDtoRequest, String token) throws Exception {
        log.info("Begin multiReceiptAfterReceipt");


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.add(CONTENT_KEY, CONTENT_TYPE);

        ServiceRequestSaveResponse res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url("http://localhost:1102/v1/createReceipt")
                .httpHeaders(httpHeaders)
                .body(receiptServiceDtoRequest)
                .typeResponseType(ServiceRequestSaveResponse.class)
                .build().call(restTemplate);

        log.info("multiReceiptAfterReceipt Response {}", res);
        log.info("End multiReceiptAfterReceipt");

        // creating consumed log api
        consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.multi_create_receipt, receiptServiceDtoRequest.getActivityData().getUserId(), receiptServiceDtoRequest, res, STATUS_MESSAGE, receiptServiceDtoRequest.getActivityData().getLoanId(), HttpMethod.POST.name(), CREATE_RECEIPT_STATUS);
        return res;
    }

    @Override
    public Object getReceiptDate(String bearerToken) throws CustomException {

        GetReceiptDateResponse getReceiptDateResponse = new GetReceiptDateResponse();
        BaseDTOResponse<Object> baseResponse;
        try {

            String businessDateConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(USE_BUSINESS_DATE_AS_RECEIPT_DATE);
            String transactionDateConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(USE_BUSINESS_DATE_AS_TRANSACTION_DATE);



            String businessDate = receiptRepository.getBusinessDateFromLmsConfiguration();
            ReceiptDateResponse receiptDateResponse = ReceiptDateResponse.builder().businessDate(businessDate).build();

//            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_receipt_date, null, null, receiptDateResponse, STATUS_MESSAGE, null, HttpMethod.POST.name(), "getReceiptDate");

            if (businessDateConf.equals("true")) {
                getReceiptDateResponse.setBusinessDate(businessDate);
            } else {
                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATS[0]);
                String cDate = dateObj.format(formatter);
                getReceiptDateResponse.setBusinessDate(cDate);
            }

            if (transactionDateConf.equals("true")) {
                getReceiptDateResponse.setTransactionDate(businessDate);
            } else {
                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATS[0]);
                String cDate = dateObj.format(formatter);
                getReceiptDateResponse.setTransactionDate(cDate);
            }
            baseResponse = new BaseDTOResponse<>(getReceiptDateResponse);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_receipt_date, null, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "getReceiptDate");
            throw new CustomException(ee.getMessage());
        }
        return baseResponse;
    }

    @Override
    public void getPdf(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws CustomException {
        String serviceReqId = null;
        String deliverableType = null;
        try {

            if (httpServletRequest.getParameter("deliverableType") != null) {
                deliverableType = httpServletRequest.getParameter("deliverableType");
            }
            if (httpServletRequest.getParameter("serviceRequestId") != null) {
                serviceReqId = httpServletRequest.getParameter("serviceRequestId");
            }

            String url = "http://localhost:1102/v1/getPdf?deliverableType=" + deliverableType + "&serviceRequestId=" + serviceReqId;

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);

            ResponseEntity<byte[]> response;


            restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    byte[].class);

            OutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(Objects.requireNonNull(response.getBody()));
            outputStream.close();
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }

    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = new GregorianCalendar();
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
    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferredForPortal(ReceiptTransferLmsFilterDTO filterDTO) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
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
                    mainData.put(TOTAL_ROWS_STR, receiptsData.get(0).get(TOTAL_ROWS_STR));
                } else {
                    mainData.put("receipts_data", new ArrayList<>());
                    mainData.put(TOTAL_ROWS_STR, 0);
                }
            }
            baseDTOResponse = new BaseDTOResponse<>(mainData);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(1017000);
            throw new CollectionException(errCode, 1017000);
        }

        return baseDTOResponse;

    }

    @Override
    public String createCollectionReceipt(Map<String, Object> requestBody, String token) throws CustomException {

        try {

            CollectionReceiptEntity collectionReceiptEntity = new CollectionReceiptEntity();
            collectionReceiptEntity.setReceiptId(Long.parseLong(requestBody.get(SERVICE_REQ_ID).toString()));
            collectionReceiptEntity.setCreatedBy(Long.parseLong(requestBody.get(COLLECTION_RECIPES_KEY[1]).toString()));
            collectionReceiptEntity.setReceiptHolderUserId(Long.parseLong(requestBody.get(COLLECTION_RECIPES_KEY[1]).toString()));
            collectionReceiptEntity.setCollectionActivityLogsId(Long.parseLong(requestBody.get(COLLECTION_RECIPES_KEY[0]).toString()));

            collectionReceiptRepository.save(collectionReceiptEntity);
            return "data_saved_successfully";
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }
    }

    private Date getBeginningDate(){
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date getEndDate(){
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        return calendar.getTime();
    }
}
