package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.common.exception.DataLockException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.AuthorizationResponse;
import com.synoriq.synofin.collection.collectionservice.rest.request.collectionincentivedtos.CollectionIncentiveRequestDTOs;
import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterdtos.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenurl.ShortenUrlDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenurl.ShortenUrlRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.getdocumentsresponsedtos.GetDocumentsDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.getdocumentsresponsedtos.GetDocumentsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.shortenurldtos.ShortenUrlResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.collateraldetailsresponsedto.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.CustomerDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.LoanBasicDetailsDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.loansummaryforloandtos.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.TaskDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdatadtos.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdetailbytokendtos.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdetailsbyuseriddtos.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.BankNameIFSCResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.ContactResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.*;
import com.synoriq.synofin.collection.collectionservice.service.printservice.PrintServiceImplementation;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.USER_MESSAGE;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;

@Service
@Slf4j
public class UtilityServiceImpl implements UtilityService {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAILURE_STATUS = "failure";
    private static final String PRE_PROD = "pre-prod";
    private static final String PREPROD_STR = "preprod";
    private static final String MODEL_QUERY_STR = "&model=";
    private static final String DATE_FORMAT_STR = "dd-MM-yyyy";
    private static final String LANGUAGE_TYPE = "english";
    private static final String URL_STR = "https://api-";
    private static final String AUTHORIZATION_STR = "Authorization";
    private static final String CONTENT_TYPE_STR = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";
    private static final String APPLICANT_STR = "applicant";
    private static final String SENDER = "FINOVA";
    private static final String PAYMENT_MODE = "cheque";
    private static final String PROFILE_PHOTO_STR = "profile_photo";
    private static final String DOCUMENT_STR = "document";
    private static final String MOBILE_STR = "917805951252";

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Autowired
    FinovaSmsService finovaSmsService;

    @Value("${spring.profiles.active}")
    private String springProfile;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private CflSmsService cflSmsService;

    @Autowired
    private PrintServiceImplementation printServiceImplementation;

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    CslSmsService cslSmsService;

    @Autowired
    LifcSmsService lifcSmsService;

    @Autowired
    private SpfcSmsService spfcSmsService;

    @Autowired
    private PaisabuddySmsService paisabuddySmsService;
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    RegisteredDeviceInfoRepository registeredDeviceInfoRepository;

    @Autowired
    private RSAUtils rsaUtils;
    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Object getMasterData(String token, MasterDtoRequest requestBody) throws CustomException {

        Object res = new Object();
        try {
            MasterDtoRequest masterBody = new ObjectMapper().convertValue(requestBody, MasterDtoRequest.class);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getMasterType")
                    .httpHeaders(httpHeaders)
                    .body(masterBody)
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, masterBody, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "getMasterType");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, requestBody, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "getMasterType");
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws CustomException {

        UserDTOResponse res;
        BaseDTOResponse<Object> baseDTOResponse = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, UserDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getAllUserData")
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDTOResponse.class)
                    .build().call(restTemplate);

            String modifiedResponse = "response: " + res.getResponse() + " error: " + res.getError();
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, convertToJSON(modifiedResponse), SUCCESS_STATUS, null, HttpMethod.GET.name(), "getAllUserData");
            List<UsersDataDTO> userData = res.getData();
            for (int i = 0; i < userData.toArray().length; i++) {
                userData.get(i).setTransferTo(userData.get(i).getName() + " - " + userData.get(i).getEmployeeCode());
            }
            log.info("userData.toArray().length {}", userData.toArray().length);
            int pageRequest = (page * size) - 10;
            List<UsersDataDTO> pageableArr = new LinkedList<>();

            if (key.equals("")) {
                for (int i = pageRequest; i < (pageRequest + 10); i++) {
                    pageableArr.add(userData.get(i));
                }
                baseDTOResponse = new BaseDTOResponse<>(pageableArr);
            } else {
                List<UsersDataDTO> filteredList = userData.
                        stream().
                        filter(user -> (user.getUsername() != null && user.getName() != null && user.getEmployeeCode() != null && user.getActive()) && (Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getUsername()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getName()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getEmployeeCode()).find())).
                        collect(Collectors.toList());
                int length;
                int filterSize = filteredList.size();
                filterSize = filterSize - pageRequest;
                if (filterSize > 10) {
                    length = (pageRequest + 10);
                } else {
                    length = filterSize + pageRequest;
                }
                for (int i = pageRequest; i < length; i++) {
                    // Set other properties if needed
                    pageableArr.add(filteredList.get(i));
                }
                baseDTOResponse = new BaseDTOResponse<>(pageableArr);
            }

        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getAllUserData");

            log.error("{}", ee.getMessage());
        }

        return baseDTOResponse;
    }

    @Override
    public Object getContactSupport(String token, String keyword, String model) throws CustomException {

        Object res = new Object();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, ContactResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getContactSupport?keyword=" + keyword + MODEL_QUERY_STR + model)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ContactResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, res, SUCCESS_STATUS, null, HttpMethod.GET.name(), "getContactSupport?keyword=" + keyword + MODEL_QUERY_STR + model);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getContactSupport?keyword=" + keyword + MODEL_QUERY_STR + model);
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public Date addOneDay(Date date) throws CustomException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
            Calendar c = Calendar.getInstance();
            String endDate = simpleDateFormat.format(date);
            c.setTime(simpleDateFormat.parse(endDate));
            c.add(Calendar.DATE, 1);  // number of days to add
            String to = simpleDateFormat.format(c.getTime());
            SimpleDateFormat simpleDateFormats = new SimpleDateFormat(DATE_FORMAT_STR);
            return simpleDateFormats.parse(to);
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public String mobileNumberMasking(String mobile) {
        String maskedNumberConfiguration = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MASKED_NUMBER_CONFIGURATION);
        if (Objects.equals(maskedNumberConfiguration, "true") && (mobile != null && !mobile.equalsIgnoreCase(""))) {
                return mobile.replaceAll(".(?=.{4})", "*");
        }
        return mobile;
    }

    @Override
    public String addSuffix(Integer i) {
        if (i != null) {
            int j = i % 10;
            int k = i % 100;
            if (j == 1 && k != 11) {
                return i + "st";
            }
            if (j == 2 && k != 12) {
                return i + "nd";
            }
            if (j == 3 && k != 13) {
                return i + "rd";
            }
            return i + "th";
        }
        return "--";
    }

    @Override
    public String capitalizeName(String name) {
        String[] strArr = name.split(" ");
        StringBuilder newStr = new StringBuilder();
        int i = 0;
        while (i < strArr.length) {
            newStr.append(strArr[i].substring(0, 1).toUpperCase()).append(strArr[i].substring(1)).append(" ");
            i++;
        }
        return newStr.toString().trim();
    }


    @Override
    public String getApiUrl() {
        if (Objects.equals(springProfile, PRE_PROD)) {
            springProfile = PREPROD_STR;
        }
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            return URL_STR + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI() + "?" + queryString;
        }
        return URL_STR + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI();
    }


    @Override
    public boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    @Override
    public Object getBankNameByIFSC(String keyword) throws CustomException {

        Object res = new Object();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, BankNameIFSCResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBankNameByIFSC?keyword=" + keyword)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(BankNameIFSCResponseDTO.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, res, SUCCESS_STATUS, null, HttpMethod.GET.name(), "getBankNameByIFSC?keyword=" + keyword);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getBankNameByIFSC?keyword=" + keyword);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token) {
        UserDetailByTokenDTOResponse res = new UserDetailByTokenDTOResponse();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, UserDetailByTokenDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getUserDetailsByToken")
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDetailByTokenDTOResponse.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, res, SUCCESS_STATUS, null, HttpMethod.GET.name(), "getUserDetailsByToken");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getUserDetailsByToken");
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public String convertToJSON(Object input) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName, String userId, String customerType, String customerName, String applicantMobileNumber, String collectedFromMobileNumber, String loanNumber, Long receiptId) throws IOException {
        if (Objects.equals(springProfile, PRE_PROD)) {
            springProfile = PREPROD_STR;
        }
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();

        String postData = "----userRefNo: " + userRefNo + ",clientId: " + clientId + ",paymentMode: " + paymentMode + ",receiptAmount: " + receiptAmount + ",fileName: " + fileName + ",userId: " + userId + ",customerType: " + customerType + ",customerName: " + customerName + ",applicantMobileNumber: " + applicantMobileNumber + ",collectedFromMobileNumber: " + collectedFromMobileNumber + ",loanNumber: " + loanNumber;
        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(imageData.getBytes());

        UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO = new UploadImageOnS3RequestDTO();
        UploadImageOnS3DataRequestDTO uploadImageOnS3DataRequestDTO = new UploadImageOnS3DataRequestDTO();
        uploadImageOnS3DataRequestDTO.setUserRefNo(userRefNo);
        uploadImageOnS3DataRequestDTO.setFileContentType("");
        uploadImageOnS3DataRequestDTO.setFileName(fileName);
        uploadImageOnS3DataRequestDTO.setFile(base64);
        uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
        uploadImageOnS3RequestDTO.setSystemId(COLLECTION);
        uploadImageOnS3RequestDTO.setUserReferenceNumber("");
        uploadImageOnS3RequestDTO.setSpecificPartnerName("");
        String[] loanId = fileName.split("_");

        boolean isProd = false;
        if (springProfile.equals("prod")) {
            isProd = true;
        }

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, UploadImageOnS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/uploadImageOnS3")
                    .body(uploadImageOnS3RequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UploadImageOnS3ResponseDTO.class)
                    .build().call(restTemplate);

            // creating api logs
            uploadImageOnS3DataRequestDTO.setFile("base64 string");
            uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, Long.parseLong(userId), uploadImageOnS3RequestDTO, res, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "uploadImageOnS3");


            ShortenUrlResponseDTO shortenUrlResponseDTO;
            ShortenUrlRequestDTO shortenUrlRequestDTO = new ShortenUrlRequestDTO();
            ShortenUrlDataRequestDTO shortenUrlDataRequestDTO = new ShortenUrlDataRequestDTO();

            shortenUrlRequestDTO.setUserReferenceNumber("");
            shortenUrlRequestDTO.setSpecificPartnerName("");
            shortenUrlRequestDTO.setSystemId(COLLECTION);
            shortenUrlDataRequestDTO.setId(res.getData().getDownloadUrl());
            shortenUrlRequestDTO.setData(shortenUrlDataRequestDTO);
            String shortenUrl = "";
            if (springProfile.equals(PRE_PROD)) {
                shortenUrl = SHORTEN_URL_PREPROD;
            } else {
                shortenUrl = SHORTEN_URL_PREPROD.replace(PREPROD_STR, springProfile);
            }

            log.info("1stsss {}", shortenUrl);

            shortenUrlResponseDTO = HTTPRequestService.<Object, ShortenUrlResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(shortenUrl)
                    .body(shortenUrlRequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ShortenUrlResponseDTO.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.shorten_url, Long.parseLong(userId), shortenUrlRequestDTO, shortenUrlResponseDTO, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), shortenUrl);
            boolean isApplicantMobileNumber = Objects.equals(applicantMobileNumber, "null") || applicantMobileNumber == null || applicantMobileNumber.contains("*");
            log.info("clientId {}", clientId);
            if (clientId.equals("finova")) {
                log.info("finova {}", clientId);
                FinovaSmsRequest finovaSmsRequest = new FinovaSmsRequest();
                if (paymentMode.equals("cash")) {
                    finovaSmsRequest.setTemplateId(FINOVA_CASH_MSG_FLOW_ID);
                } else if (paymentMode.equals(PAYMENT_MODE)) {
                    finovaSmsRequest.setTemplateId(FINOVA_CHEQUE_MSG_FLOW_ID);
                } else if (paymentMode.equals("upi")){
                    finovaSmsRequest.setTemplateId(FINOVA_UPI_MSG_FLOW_ID);
                }
                if (customerType.equals(APPLICANT_STR)) {
                    finovaSmsRequest.setSender(SENDER);
                    finovaSmsRequest.setShortUrl("1");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + applicantMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles(MOBILE_STR);
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData() != null ? shortenUrlResponseDTO.getData().getResult() : null);

                    FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
                    saveSendSMSActivityData(loanId, res, userId);
                    // creating api logs
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");

                } else {
                    finovaSmsRequest.setSender(SENDER);
                    finovaSmsRequest.setShortUrl("1");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + applicantMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles(MOBILE_STR);
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                    finovaSmsRequest.setSender(SENDER);
                    finovaSmsRequest.setShortUrl("1");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + collectedFromMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles(MOBILE_STR);
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                    FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
                    saveSendSMSActivityData(loanId, res, userId);
                    // creating api logs
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
                }

            }

            if (clientId.equals("csl")) {
                String decodedString = URLDecoder.decode(CSL_TEMPLATE_ENCODED_MESSAGE, StandardCharsets.UTF_8);
                String message = decodedString.replace("Vinay", customerName);
                message = message.replace("500", receiptAmount);
                message = message.replace("1-1-2032", String.valueOf(new Date()));
                message = message.replace("1234567", loanNumber);
                message = message.replace("6778990000", shortenUrlResponseDTO.getData().getResult());
                String receivedMobileNumber;
                if (isProd) {
                    if (isApplicantMobileNumber) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                } else {
                    receivedMobileNumber = "9649916989";
                }
                String encodedMessageString = URLEncoder.encode(message, StandardCharsets.UTF_8);
                String postField = "user=CSLFIN&message=" + encodedMessageString + "&key=974130e696XX&mobile=" + receivedMobileNumber + "&senderid=CSLSME&accusage=1&tempid=1707165942499421494&entityid=1701159697926729192&unicode=1";
                String smsServiceResponse = cslSmsService.sendSmsCsl(postField);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), convertToJSON((postField + postData)), convertToJSON(smsServiceResponse), SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("spfc")) {
                RequestDataDTO requestDataDTO = new RequestDataDTO();
                if (paymentMode.equals("cash")) {
                    requestDataDTO.setTemplateName("template3");
                } else if (paymentMode.equals(PAYMENT_MODE)) {
                    requestDataDTO.setTemplateName("template2");
                } else {
                    requestDataDTO.setTemplateName("template1");
                }

                requestDataDTO.setMessageType("text");
                List<SmsListDTO> smsListDTOS = new ArrayList<>();
                List<String> strings = new ArrayList<>();
                strings.add(receiptAmount);
                strings.add(loanNumber);
                strings.add(shortenUrlResponseDTO.getData().getResult());
                SmsListDTO smsListDTO = new SmsListDTO();
                smsListDTO.setMessageType(LANGUAGE_TYPE);
                String receivedMobileNumber;
                if (isProd) {
                    if (isApplicantMobileNumber) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                    smsListDTO.setMobiles("91" + receivedMobileNumber);
                } else {
                    smsListDTO.setMobiles("919887432524");
                }
                smsListDTOS.add(smsListDTO);
                requestDataDTO.setTemplateVariable(strings);
                requestDataDTO.setSmsList(smsListDTOS);
                SpfcSmsRequestDTO spfcSmsRequestDTO = new SpfcSmsRequestDTO();
                spfcSmsRequestDTO.setSystemId(COLLECTION);
                spfcSmsRequestDTO.setUserReferenceNumber("");
                spfcSmsRequestDTO.setSpecificPartnerName("");
                spfcSmsRequestDTO.setData(requestDataDTO);

                SpfcMsgDTOResponse spfcMsgDTOResponse = spfcSmsService.sendSmsSpfc(spfcSmsRequestDTO, token, springProfile);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), spfcSmsRequestDTO, spfcMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("paisabuddy")) {
                PaisabuddySmsRequest paisabuddySmsRequest = new PaisabuddySmsRequest();
                paisabuddySmsRequest.setTemplateId(PAISABUDDY_SMS_TEMPLATE_ID);
                paisabuddySmsRequest.setSender("PaisBd");
                paisabuddySmsRequest.setCustomerName(customerName);
                paisabuddySmsRequest.setAmount(receiptAmount);
                paisabuddySmsRequest.setReceiptId(String.valueOf(receiptId));
                paisabuddySmsRequest.setLoanNumber(loanNumber);
                paisabuddySmsRequest.setShortenUrl(shortenUrlResponseDTO.getData().getResult());
                if (isProd) {
                    paisabuddySmsRequest.setMobiles("91" + collectedFromMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                } else {
                    paisabuddySmsRequest.setMobiles("919773354037");
                }

                PaisabuddyMsgDTOResponse paisabuddyMsgDTOResponse = paisabuddySmsService.sendSmsPaisabuddy(paisabuddySmsRequest);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), paisabuddySmsRequest, paisabuddyMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("cfl")) {

                log.info("in iffff");
                RequestDataDTO requestDataDTO = new RequestDataDTO();
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STR);
                String receiptDate = formatter.format(new Date());
                requestDataDTO.setTemplateName("template5");

                requestDataDTO.setMessageType("text");
                List<SmsListDTO> smsListDTOS = new ArrayList<>();
                List<String> strings = new ArrayList<>();
                strings.add(receiptAmount);
                strings.add(loanNumber);
                strings.add(receiptDate);
                strings.add(shortenUrlResponseDTO.getData().getResult());
                SmsListDTO smsListDTO = new SmsListDTO();
                smsListDTO.setMessageType(LANGUAGE_TYPE);
                String receivedMobileNumber;
                if (isProd) {
                    if (isApplicantMobileNumber) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                    smsListDTO.setMobiles("91" + receivedMobileNumber);
                } else {
                    smsListDTO.setMobiles("91" + collectedFromMobileNumber);
                }
                smsListDTOS.add(smsListDTO);
                requestDataDTO.setTemplateVariable(strings);
                requestDataDTO.setSmsList(smsListDTOS);
                CflSmsRequest cflSmsRequest = new CflSmsRequest();
                cflSmsRequest.setSystemId(COLLECTION);
                cflSmsRequest.setUserReferenceNumber("");
                cflSmsRequest.setSpecificPartnerName("");
                cflSmsRequest.setData(requestDataDTO);

                CflMsgDTOResponse cflMsgDTOResponse = cflSmsService.sendSmsCfl(cflSmsRequest, token, springProfile);
                log.info("cflMsgDTOResponse {}", cflMsgDTOResponse);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), cflSmsRequest, cflMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("lifc")) {

                log.info("in iffff");
                RequestDataDTO requestDataDTO = new RequestDataDTO();
                requestDataDTO.setTemplateName("template1");

                requestDataDTO.setMessageType("text");
                List<SmsListDTO> smsListDTOS = new ArrayList<>();
                List<String> strings = new ArrayList<>();
                strings.add(receiptAmount);
                strings.add(loanNumber);
                strings.add(String.valueOf(receiptId));
                strings.add(shortenUrlResponseDTO.getData().getResult());
                SmsListDTO smsListDTO = new SmsListDTO();
                smsListDTO.setMessageType(LANGUAGE_TYPE);
                String receivedMobileNumber;
                if (isProd) {
                    if (isApplicantMobileNumber) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                    smsListDTO.setMobiles("91" + receivedMobileNumber);
                } else {
                    smsListDTO.setMobiles("91" + collectedFromMobileNumber);
                }
                smsListDTOS.add(smsListDTO);
                requestDataDTO.setTemplateVariable(strings);
                requestDataDTO.setSmsList(smsListDTOS);
                LifcSmsRequest lifcSmsRequest = new LifcSmsRequest();
                lifcSmsRequest.setSystemId(COLLECTION);
                lifcSmsRequest.setUserReferenceNumber("");
                lifcSmsRequest.setSpecificPartnerName("");
                lifcSmsRequest.setData(requestDataDTO);

                LifcMsgDTOResponse lifcMsgDTOResponse = lifcSmsService.sendSmsLifc(lifcSmsRequest, token, springProfile);
                log.info("cflMsgDTOResponse {}", lifcMsgDTOResponse);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), lifcSmsRequest, lifcMsgDTOResponse, SUCCESS_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }




        } catch (Exception ee) {
            log.info("ee messages", ee);
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), null, modifiedErrorMessage, FAILURE_STATUS, Long.parseLong(loanId[0]), HttpMethod.POST.name(), "sendPdfOnCustomer");
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    private void saveSendSMSActivityData(String[] loanId, UploadImageOnS3ResponseDTO res, String userId) {
        String loanApplicationNumber = taskRepository.getLoanApplicationNumber(Long.parseLong(loanId[0]));
        String remarks = USER_MESSAGE;
        remarks = remarks.replace("{loan_number}", loanApplicationNumber);

        CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
        collectionActivityLogsEntity.setActivityName("send_message_to_user");
        collectionActivityLogsEntity.setActivityDate(new Date());
        collectionActivityLogsEntity.setDeleted(false);
        collectionActivityLogsEntity.setActivityBy(Long.parseLong(userId));
        collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
        collectionActivityLogsEntity.setAddress("{}");
        collectionActivityLogsEntity.setRemarks(remarks);
        collectionActivityLogsEntity.setImages(res.getData());
        collectionActivityLogsEntity.setLoanId(Long.parseLong(loanId[0]));
        collectionActivityLogsEntity.setGeolocation("{}");

        collectionActivityLogsRepository.save(collectionActivityLogsEntity);
    }

    @Override
    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId) {
        UserDetailByUserIdDTOResponse res = new UserDetailByUserIdDTOResponse();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, UserDetailByUserIdDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getUserDetailsByUserId?userId=" + userId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDetailByUserIdDTOResponse.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, res, SUCCESS_STATUS, null, HttpMethod.GET.name(), "getUserDetailsByUserId?userId=" + userId);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getUserDetailsByUserId?userId=" + userId);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public Object getThermalPrintData(String receiptId) throws CollectionException {

        ThermalPrintDataDTO thermalPrintDataDTO = new ThermalPrintDataDTO();
        String base64 = "";

        Map<String, Object> serviceRequestData = receiptRepository.getServiceRequestData(Long.parseLong(receiptId));
        if (serviceRequestData != null) {

            String dateTime = String.valueOf(serviceRequestData.get("created_date"));
            dateTime = dateTime.substring(0, dateTime.lastIndexOf("."));
            String[] splitDateTime = dateTime.split(" ");
            // date format change
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = simpleDateFormat.parse(splitDateTime[0]);
            } catch (ParseException e) {
                throw new CustomException(e.getMessage());
            }
            SimpleDateFormat newDateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
            String newFormatDate = newDateFormat.format(date);

            String newDate = newFormatDate + " " + splitDateTime[1];

            String paymentMode = String.valueOf(serviceRequestData.get("payment_mode"));
            if (paymentMode.equals("upi")) {
                paymentMode = "UPI/NEFT";
            } else if (paymentMode.equals("cash")) {
                paymentMode = "Cash";
            } else if (paymentMode.equals(PAYMENT_MODE)) {
                paymentMode = "Cheque";
            }

            thermalPrintDataDTO.setDateTime(newDate);
            thermalPrintDataDTO.setBranchName(String.valueOf(serviceRequestData.get("branch_name")));
            thermalPrintDataDTO.setTransactionNumber(String.valueOf(serviceRequestData.get("transaction_reference")));
            thermalPrintDataDTO.setReceiptNo(String.valueOf(serviceRequestData.get("receipt_no")));
            thermalPrintDataDTO.setCollectedFrom(String.valueOf(serviceRequestData.get("collected_from")));
            thermalPrintDataDTO.setLoanAmount(String.valueOf(serviceRequestData.get("loan_amount")));
            thermalPrintDataDTO.setPaymentMode(paymentMode);
            thermalPrintDataDTO.setCustomerName(String.valueOf(serviceRequestData.get("customer_name")));
            thermalPrintDataDTO.setMobileNumber(String.valueOf(serviceRequestData.get("mobile_number")));
            thermalPrintDataDTO.setIfsc(String.valueOf(serviceRequestData.get("ifsc")));
            thermalPrintDataDTO.setChequeNo(String.valueOf(serviceRequestData.get("cheque_no")));
            thermalPrintDataDTO.setBankName(String.valueOf(serviceRequestData.get("bank_name")));
            thermalPrintDataDTO.setBankAccountNumber(String.valueOf(serviceRequestData.get("bank_account_number")));
            thermalPrintDataDTO.setLoanNumber(String.valueOf(serviceRequestData.get("loan_number")));
            thermalPrintDataDTO.setUserCode(String.valueOf(serviceRequestData.get("user_code")));
            thermalPrintDataDTO.setUserName(String.valueOf(serviceRequestData.get("user_name")));
            thermalPrintDataDTO.setActualEmi(String.valueOf(serviceRequestData.get("actual_emi")));
            thermalPrintDataDTO.setReceiptAmount(String.valueOf(serviceRequestData.get("receipt_amount")));
            thermalPrintDataDTO.setTotal(String.valueOf(serviceRequestData.get("total")));

            byte[] receiptBytes = null;
            try {
                receiptBytes = printServiceImplementation.printDesign(thermalPrintDataDTO, currentUserInfo.getClientId());
            } catch (Exception e) {
                throw new CustomException(e.getMessage());
            }
            base64 = Base64.getEncoder().encodeToString(receiptBytes);
        } else {
            ErrorCode errCode = ErrorCode.getErrorCode(1017002);
            throw new CollectionException(errCode, 1017002);
        }

        return new BaseDTOResponse<>(base64);
    }

    public String splitCodeName(String codeName) {
        StringBuilder ans = new StringBuilder();
        ans.append(codeName);
        String patternString = "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(codeName);
        while (matcher.find()) {
            String matchedSubstring = matcher.group(1);
            ans.replace(0, ans.length(), matchedSubstring);
        }
        return ans.toString();
    }

    public String getTokenByApiKeySecret(Map<String, Object> map) throws CustomException {
        String gateWayUrl2 = Objects.equals(springProfile, "prod") ? "prod2" : springProfile;
        String gateWayUrl =URL_STR + (Objects.equals(springProfile, PRE_PROD) ? PREPROD_STR : gateWayUrl2) + ".synofin.tech/";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Source", String.valueOf(map.get("client")));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(String.valueOf(map.get("api_key")), String.valueOf(map.get("api_secret")));
        // deccantest credentials

        String url = gateWayUrl + "oauth/authorization";
        MasterDTOResponse response = new MasterDTOResponse();
        log.info("httpHeaders - {}", httpHeaders);
        log.info("url - {}", url);
        try {
            response = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST).httpHeaders(httpHeaders)
                    .url(url)
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call(restTemplate);
        } catch (Exception e) {
            log.error("error in generate token api", e);
            e.printStackTrace();
        }
        log.info("response - {}", response);
        AuthorizationResponse authorizationResponse = new ObjectMapper().convertValue(response.getData(), AuthorizationResponse.class);
        return authorizationResponse.getAuthData().getAccessToken();
    }

    @Override
    public BaseDTOResponse<Object> getDocuments(String token, String loanId) throws CustomException {
        GetDocumentsResponseDTO res;
        List<Map<String, Object>> documentsDataArr = new ArrayList<>();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_STR, token);
            httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);

            res = HTTPRequestService.<Object, GetDocumentsResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getDocuments?loanId=" + loanId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(GetDocumentsResponseDTO.class)
                    .build().call(restTemplate);

            log.info("res {}", res);

            if (!res.getData().isEmpty()) {
                for (GetDocumentsDataResponseDTO getDocumentsDataResponseDTO : res.getData()) {
                    Map<String, Object> documentsData = new HashMap<>();
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), APPLICANT_STR) && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), PROFILE_PHOTO_STR)) {
                        documentsData.put("type", APPLICANT_STR);
                        documentsData.put(DOCUMENT_STR, getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), "coapplicant") && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), PROFILE_PHOTO_STR)) {
                        documentsData.put("type", "coapplicant");
                        documentsData.put(DOCUMENT_STR, getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), "guarantor") && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), PROFILE_PHOTO_STR)) {
                        documentsData.put("type", "guarantor");
                        documentsData.put(DOCUMENT_STR, getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                }
            }

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, res, SUCCESS_STATUS, null, HttpMethod.GET.name(), "getDocuments?loanId=" + loanId);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "getDocuments?loanId=" + loanId);
            log.error("{}", ee.getMessage());
        }

        return new BaseDTOResponse<>(documentsDataArr);
    }

    @Override
    public List<Map<String, Object>> formatDigitalSiteVisitData(List<Tuple> data) throws CustomException {
        final List<Map<String, Object>> formattedRows = new ArrayList<>();
        try {
            data.forEach(row -> {
                final Map<String, Object> formattedRow = new HashMap<>();
                row.getElements().forEach(column -> {

                    final String columnName = column.getAlias();
                    Object columnValue = row.get(column);
                    formattedRow.put(columnName, columnValue);
                });

                formattedRows.add(formattedRow);
            });

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return formattedRows;
    }

    @Override
    public BaseDTOResponse<Object> getCollaterals(Long loanIdNumber, String token) throws CustomException {
        CollateralDetailsResponseDTO collateralResponse = new CollateralDetailsResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_STR, token);
        httpHeaders.add(CONTENT_TYPE_STR, CONTENT_TYPE);
        try {
            collateralResponse = HTTPRequestService.<Object, CollateralDetailsResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCollaterals?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(CollateralDetailsResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, collateralResponse, SUCCESS_STATUS, loanIdNumber, HttpMethod.GET.name(), "getCollaterals?loanId=" + loanIdNumber);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, modifiedErrorMessage, FAILURE_STATUS, loanIdNumber, HttpMethod.GET.name(), "getCollaterals?loanId=" + loanIdNumber);
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(collateralResponse);
    }

    @Override
    public BaseDTOResponse<Object> employeeMobileNumberValidator(String token, String mobileNumber) throws CustomException {
        try {
            String mobileNumberValidationConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(EMPLOYEE_MOBILE_NUMBER_VALIDATION);
            if(mobileNumberValidationConf.equals("true")) {
                String employeeMobileNumber = registeredDeviceInfoRepository.getEmployeeMobileNumber(mobileNumber);
                if(!Objects.equals(employeeMobileNumber, null)) {
                    return new BaseDTOResponse<>("Match Found");
                }
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>("No Match Found");
    }

    @Override
    public Object getBankAccountDetails(Long bankAccountId) {
        return receiptRepository.getBankAccountDetails(bankAccountId);
    }


    @Override
    public BaseDTOResponse<Object> checkTransactionReferenceNumber(String token, String transactionReferenceNumber) throws CustomException {
        try {
            Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(transactionReferenceNumber);
            if (!transactionNumberCheck.isEmpty()) {
                return new BaseDTOResponse<>("UTR number already exist");
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>("UTR number not found");
    }

    @Override
    public TaskDetailDTOResponse getChargesForLoan(String token, TaskDetailRequestDTO loanDataBody) throws CustomException {
        TaskDetailDTOResponse loanRes = new TaskDetailDTOResponse();
        try {
            HttpHeaders httpHeaders = UtilityService.createHeaders(token);
            log.info("httpHeaders {}", httpHeaders);

            loanRes = HTTPRequestService.<Object, TaskDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getDataForLoanActions")
                    .httpHeaders(httpHeaders)
                    .body(loanDataBody)
                    .typeResponseType(TaskDetailDTOResponse.class)
                    .build().call(restTemplate);

            log.info("loan details jhadsuhbsduh {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, loanRes, SUCCESS_STATUS, Long.parseLong(loanDataBody.getRequestData().getLoanId()), HttpMethod.POST.name(), "getDataForLoanActions");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, modifiedErrorMessage, FAILURE_STATUS, Long.parseLong(loanDataBody.getRequestData().getLoanId()), HttpMethod.GET.name(), "getDataForLoanActions");
            log.error("{}", e.getMessage());
        }
        return loanRes;
    }

    @Override
    public LoanBasicDetailsDTOResponse getBasicLoanDetails(String token, Long loanId) throws CustomException {
        LoanBasicDetailsDTOResponse loanRes = new LoanBasicDetailsDTOResponse();
        try {
            loanRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(LoanBasicDetailsDTOResponse.class)
                    .build().call(restTemplate);

            log.info("loan details jhadsuhbsduh {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, loanRes, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getBasicLoanDetails?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, modifiedErrorMessage, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getBasicLoanDetails?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return loanRes;
    }

    @Override
    public CustomerDetailDTOResponse getCustomerDetails(String token, Long loanId) throws CustomException {
        CustomerDetailDTOResponse customerRes = new CustomerDetailDTOResponse();
        try {
            customerRes = HTTPRequestService.<Object, CustomerDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCustomerDetails?loanId=" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(CustomerDetailDTOResponse.class)
                    .build().call(restTemplate);

            log.info("customerRes details {}", customerRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_customer_details, null, null, customerRes, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getCustomerDetails?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_customer_details, null, null, modifiedErrorMessage, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getCustomerDetails?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return customerRes;
    }

    @Override
    public LoanSummaryResponseDTO getLoanSummary(String token, Long loanId) throws CustomException {
        LoanSummaryResponseDTO summaryRes = new LoanSummaryResponseDTO();
        try {
            summaryRes = HTTPRequestService.<Object, LoanSummaryResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getLoanSummaryForLoan/" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(LoanSummaryResponseDTO.class)
                    .build().call(restTemplate);

            log.info("summaryRes {}", summaryRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_loan_summary, null, null, summaryRes, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getLoanSummaryForLoan?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_loan_summary, null, null, modifiedErrorMessage, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getLoanSummaryForLoan?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return summaryRes;
    }

    @Override
    public Object getCollectionIncentiveData(String token, CollectionIncentiveRequestDTOs collectionIncentiveRequestDTOs) throws CustomException {

        try {

            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;
            List<Map<String, Object>> response = new ArrayList<>();
            List<Map<String, Object>> collectionDetails = receiptRepository.getCollectionIncentiveUsers();

            String dateWhereClause = " ";
            if (collectionIncentiveRequestDTOs.getStartDate() != null) {
                dateWhereClause = " and sr.created_date between " + "'" + collectionIncentiveRequestDTOs.getStartDate() + "'" + " and " + "'" + collectionIncentiveRequestDTOs.getEndDate() + "'";
            }
            for (Map<String, Object> user : collectionDetails) {

                Query query = entityManager.createNativeQuery(
                        "select\n" +
                                "\tconcat(lms.decrypt_data(c.first_name, ?1, ?2, ?3), ' ', lms.decrypt_data(c.last_name, ?1, ?2, ?3)) as customer_name,\n" +
                                "\tla.loan_application_number as loan_account_number,\n" +
                                "\tla.disbursal_date,\n" +
                                "\tla.branch,\n" +
                                "\tla.emi_amount,\n" +
                                "\tla.disbursed_amount as loan_amount,\n" +
                                "\tla.installment_plan,\n" +
                                "\tla.due_day as emi_period\n" +
                                "from\n" +
                                "\tcollection.collection_receipts cr\n" +
                                "join lms.service_request sr on\n" +
                                "\tcr.receipt_id = sr.service_request_id\n" +
                                "join lms.loan_application la on\n" +
                                "\tsr.loan_id = la.loan_application_id\n" +
                                "join lms.customer_loan_mapping clm on\n" +
                                "\tla.loan_application_id = clm.loan_id\n" +
                                "join lms.customer c on\n" +
                                "\tclm.customer_id = c.customer_id\n" +
                                "where\n" +
                                "\tsr.created_by = (\n" +
                                "\tselect\n" +
                                "\t\tuser_id\n" +
                                "\tfrom\n" +
                                "\t\tmaster.users\n" +
                                "\twhere\n" +
                                "\t\tusername = ?4) " + dateWhereClause, Tuple.class
                );

                query.setParameter(1, encryptionKey);
                query.setParameter(2, password);
                query.setParameter(3, piiPermission);
                query.setParameter(4, String.valueOf(user.get("co_id")));
                List<Tuple> userLoanDetails = query.getResultList();
                Map<String, Object> newUser = new HashMap<>(user);
                newUser.put("loan_details", this.formatDigitalSiteVisitData(userLoanDetails));
                response.add(newUser);
            }

            log.info("response {}", response);

            return response;
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }
    }


    @Override
    public void createReceiptByCallBack(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity, String token, Map<String, Object> mainResponse, String utrNumber) throws CustomException, DataLockException, InterruptedException, IOException {
        log.info("Begin callback create receipt function");
        try {

            // implementing create receipt here
            String receiptDate = receiptRepository.getBusinessDateFromLmsConfiguration();
            ReceiptServiceDtoRequest receiptServiceDtoRequest = new ObjectMapper().convertValue(digitalPaymentTransactionsEntity.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            receiptServiceDtoRequest.getRequestData().getRequestData().setTransactionReference(utrNumber);
            // explicitly setting date of receipt whenever callback received or status transaction called
            receiptServiceDtoRequest.getRequestData().getRequestData().setDateOfReceipt(receiptDate);
            receiptServiceDtoRequest.getRequestData().getRequestData().setTransactionDate(receiptDate);
            ServiceRequestSaveResponse resp = this.getServiceRequestSaveResponseParent(receiptServiceDtoRequest, token);

            log.info("receipt response {}", resp);

            digitalPaymentTransactionsEntity.setReceiptResponse(resp);
            if (Boolean.FALSE.equals(resp.getResponse())) {
                throw new CustomException(resp.getError().getText(), Integer.parseInt(resp.getError().getCode()));
            }
            if (resp.getData() != null && resp.getData().getServiceRequestId() != null) {
                log.info("in ifff receipt response {}", resp);
                mainResponse.replace(RECEIPT_GENERATED, true);
                mainResponse.replace(SR_ID, resp.getData().getServiceRequestId());
                digitalPaymentTransactionsEntity.setReceiptGenerated(true);
                String url = GET_PDF_API + resp.getData().getServiceRequestId();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBearerAuth(token);

                ResponseEntity<byte[]> res;

                restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
                res = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(httpHeaders),
                        byte[].class);


                byte[] byteArray = res.getBody();
                String filename = "file.jpg";
                int length = byteArray == null ? 0 : byteArray.length;
                DiskFileItem fileItem = new DiskFileItem("file", "application/pdf", true, filename, length, new java.io.File(System.getProperty("java.io.tmpdir")));
                fileItem.getOutputStream().write(byteArray);

                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                String updatedFileName = receiptServiceDtoRequest.getRequestData().getLoanId() + "_" + new Date().getTime() + "_receipt_image.pdf";
                String userRef = "receipt/" + receiptServiceDtoRequest.getRequestData().getRequestData().getCreatedBy();
                // hitting send sms to customer
                this.sendPdfToCustomerUsingS3(token, multipartFile, userRef, currentUserInfo.getClientId(), receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode(), receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount(), updatedFileName,
                            receiptServiceDtoRequest.getActivityData().getUserId().toString(), receiptServiceDtoRequest.getCustomerType(),
                            receiptServiceDtoRequest.getCustomerName(), receiptServiceDtoRequest.getApplicantMobileNumber(), receiptServiceDtoRequest.getCollectedFromNumber(), receiptServiceDtoRequest.getLoanApplicationNumber(), resp.getData().getServiceRequestId());

                log.info("in callback create receipt function ending");
            } else {
                mainResponse.replace(SR_ID, null);
                mainResponse.replace(RECEIPT_GENERATED, false);
            }
        } catch (CustomException ee) {
            throw new CustomException(ee.getText(), ee.getCode());
        }
        log.info("Ending callback create receipt function");
    }

    private static MultipartFile createBlankMultiPartFile() {
        return new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File file) throws IOException, IllegalStateException {
                log.info("This method is empty because we are initializing a empty multipart file for business use");
            }
        };
    }

    private ServiceRequestSaveResponse getServiceRequestSaveResponseParent(ReceiptServiceDtoRequest receiptServiceDtoRequest, String token) throws InterruptedException, DataLockException {
        ServiceRequestSaveResponse resp = null;
        resp = this.getServiceRequestSaveResponseChild(receiptServiceDtoRequest, token);
        return resp;
    }

    private ServiceRequestSaveResponse getServiceRequestSaveResponseChild(ReceiptServiceDtoRequest receiptServiceDtoRequest, String token) throws InterruptedException, DataLockException {
        ServiceRequestSaveResponse resp = null;
            try {
               resp = receiptService.createReceiptNew(receiptServiceDtoRequest, createBlankMultiPartFile(), createBlankMultiPartFile(), token, true);
            } catch (JsonProcessingException e) {
                throw new CustomException(e.getMessage());
            }
        return resp;
    }

}
