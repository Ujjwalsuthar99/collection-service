package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.AuthorizationResponse;
import com.synoriq.synofin.collection.collectionservice.rest.request.collectionIncentiveDTOs.CollectionIncentiveRequestDTOs;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3ImageDTOs.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3ImageDTOs.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs.GetDocumentsDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs.GetDocumentsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.ShortenUrlDTOs.ShortenUrlResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CustomerDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanBasicDetailsDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.TaskDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDataDTOs.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.BankNameIFSCResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.ContactResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.*;
import com.synoriq.synofin.collection.collectionservice.service.printService.PrintServiceImplementation;
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

    @Override
    public Object getMasterData(String token, MasterDtoRequest requestBody) throws Exception {

        Object res = new Object();
        try {
            MasterDtoRequest masterBody = new ObjectMapper().convertValue(requestBody, MasterDtoRequest.class);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getMasterType")
                    .httpHeaders(httpHeaders)
                    .body(masterBody)
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call();

//            log.info("responseData {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, masterBody, res, "success", null, HttpMethod.POST.name(), "getMasterType");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, requestBody, modifiedErrorMessage, "failure", null, HttpMethod.POST.name(), "getMasterType");
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws Exception {

        UserDTOResponse res;
        BaseDTOResponse<Object> baseDTOResponse = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, UserDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getAllUserData")
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDTOResponse.class)
                    .build().call();

            String modifiedResponse = "response: " + res.getResponse() + " error: " + res.getError();
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, convertToJSON(modifiedResponse), "success", null, HttpMethod.GET.name(), "getAllUserData");
            List<UsersDataDTO> userData = res.getData();
            for (int i = 0; i < userData.toArray().length; i++) {
                userData.get(i).setTransferTo(userData.get(i).getName() + " - " + userData.get(i).getEmployeeCode());
            }
            log.info("userData.toArray().length {}", userData.toArray().length);
            int pageRequest = (page * size) - 10;
            List<UsersDataDTO> pageableArr = new LinkedList<>();

//            List<UsersDataDTO> filteredList = userData.parallelStream().filter(user -> (user.getUsername().contains(key) || user.getName().contains(key))).collect(Collectors.toList());
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getAllUserData");

            log.error("{}", ee.getMessage());
        }

        return baseDTOResponse;
    }

    @Override
    public Object getContactSupport(String token, String keyword, String model) throws Exception {

        Object res = new Object();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ContactResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getContactSupport?keyword=" + keyword + "&model=" + model)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ContactResponseDTO.class)
                    .build().call();

//            log.info("responseData {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, res, "success", null, HttpMethod.GET.name(), "getContactSupport?keyword=" + keyword + "&model=" + model);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getContactSupport?keyword=" + keyword + "&model=" + model);
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public Date addOneDay(Date date) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String endDate = simpleDateFormat.format(date);
        c.setTime(simpleDateFormat.parse(endDate));
        c.add(Calendar.DATE, 1);  // number of days to add
        String to = simpleDateFormat.format(c.getTime());
        SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormats.parse(to);
    }

    @Override
    public String mobileNumberMasking(String mobile) {
        String maskedNumberConfiguration = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MASKED_NUMBER_CONFIGURATION);
        if (Objects.equals(maskedNumberConfiguration, "true")) {
            if (mobile != null && !mobile.equalsIgnoreCase("")) {
                return mobile.replaceAll(".(?=.{4})", "*");
            }
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
        String newStr = "";
        for (int i = 0; i < strArr.length; i++) {
            newStr += strArr[i].substring(0, 1).toUpperCase() + strArr[i].substring(1) + " ";

//            newStr.append(strArr[i].substring(0, 1).toUpperCase()).append(strArr[i].substring(1, strArr[i].length)).append(" ");
        }
        return newStr.trim();
    }


    @Override
    public String getApiUrl() {
        if (Objects.equals(springProfile, "pre-prod")) {
            springProfile = "preprod";
        }
        String queryString = httpServletRequest.getQueryString();
//        httpServletRequest.getRequestURI() = "/collection-service/v1/getMasterType";
//        log.info("queryString {}", queryString);
        if (queryString != null) {
            return "https://api-" + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI() + "?" + queryString;
        }
        return "https://api-" + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI();
    }


    @Override
    public boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    @Override
    public Object getBankNameByIFSC(String keyword) throws Exception {

        Object res = new Object();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, BankNameIFSCResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBankNameByIFSC?keyword=" + keyword)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(BankNameIFSCResponseDTO.class)
                    .build().call();

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, res, "success", null, HttpMethod.GET.name(), "getBankNameByIFSC?keyword=" + keyword);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getBankNameByIFSC?keyword=" + keyword);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token) {
        UserDetailByTokenDTOResponse res = new UserDetailByTokenDTOResponse();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, UserDetailByTokenDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getUserDetailsByToken")
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDetailByTokenDTOResponse.class)
                    .build().call();

//            log.info("responseData {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, res, "success", null, HttpMethod.GET.name(), "getUserDetailsByToken");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getUserDetailsByToken");
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public String convertToJSON(String input) {
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
        if (Objects.equals(springProfile, "pre-prod")) {
            springProfile = "preprod";
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
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, UploadImageOnS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/uploadImageOnS3")
                    .body(uploadImageOnS3RequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UploadImageOnS3ResponseDTO.class)
                    .build().call();

            // creating api logs
            uploadImageOnS3DataRequestDTO.setFile("base64 string");
            uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, Long.parseLong(userId), uploadImageOnS3RequestDTO, res, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "uploadImageOnS3");


            ShortenUrlResponseDTO shortenUrlResponseDTO;
            ShortenUrlRequestDTO shortenUrlRequestDTO = new ShortenUrlRequestDTO();
            ShortenUrlDataRequestDTO shortenUrlDataRequestDTO = new ShortenUrlDataRequestDTO();

            shortenUrlRequestDTO.setUserReferenceNumber("");
            shortenUrlRequestDTO.setSpecificPartnerName("");
            shortenUrlRequestDTO.setSystemId(COLLECTION);
            shortenUrlDataRequestDTO.setId(res.getData().getDownloadUrl());
            shortenUrlRequestDTO.setData(shortenUrlDataRequestDTO);
            String shortenUrl = "";
            if (springProfile.equals("pre-prod")) {
                shortenUrl = SHORTEN_URL_PREPROD;
            } else {
                shortenUrl = SHORTEN_URL_PREPROD.replace("preprod", springProfile);
            }

            log.info("1stsss {}", shortenUrl);

            shortenUrlResponseDTO = HTTPRequestService.<Object, ShortenUrlResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(shortenUrl)
                    .body(shortenUrlRequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ShortenUrlResponseDTO.class)
                    .build().call();

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.shorten_url, Long.parseLong(userId), shortenUrlRequestDTO, shortenUrlResponseDTO, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), shortenUrl);

            log.info("clientId {}", clientId);
            if (clientId.equals("finova")) {
                log.info("finova {}", clientId);
                FinovaSmsRequest finovaSmsRequest = new FinovaSmsRequest();
                if (paymentMode.equals("cash")) {
                    finovaSmsRequest.setTemplateId(FINOVA_CASH_MSG_FLOW_ID);
                } else if (paymentMode.equals("cheque")) {
                    finovaSmsRequest.setTemplateId(FINOVA_CHEQUE_MSG_FLOW_ID);
                } else {
                }
                if (customerType.equals("applicant")) {
                    finovaSmsRequest.setSender("FINOVA");
                    finovaSmsRequest.setShortUrl("0");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + applicantMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles("917805951252");
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData() != null ? shortenUrlResponseDTO.getData().getResult() : null);

                    FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
                    saveSendSMSActivityData(loanId, res, userId);
//                    log.info("sms service for applicant finova {}", finovaMsgDTOResponse);
                    // creating api logs
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");

                } else {
                    finovaSmsRequest.setSender("FINOVA");
                    finovaSmsRequest.setShortUrl("0");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + applicantMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles("917805951252");
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                    FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
//                    log.info("sms service for applicant & collected from finova {}", finovaMsgDTOResponse);


                    finovaSmsRequest.setSender("FINOVA");
                    finovaSmsRequest.setShortUrl("0");
                    if (isProd) {
                        finovaSmsRequest.setMobiles("91" + collectedFromMobileNumber); // uncomment this line and comment above static mobile number line while going live with CSL
                    } else {
                        finovaSmsRequest.setMobiles("917805951252");
                    }
                    finovaSmsRequest.setAmount(receiptAmount);
                    finovaSmsRequest.setLoanNumber(loanNumber);
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                    finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
//                    log.info("sms service for collected from finova {}", finovaMsgDTOResponse);
                    saveSendSMSActivityData(loanId, res, userId);
                    // creating api logs
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
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
                    if (Objects.equals(applicantMobileNumber, "null") || applicantMobileNumber == null) {
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), convertToJSON((postField + postData)), convertToJSON(smsServiceResponse), "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("spfc")) {
                RequestDataDTO requestDataDTO = new RequestDataDTO();
                if (paymentMode.equals("cash")) {
                    requestDataDTO.setTemplateName("template3");
                } else if (paymentMode.equals("cheque")) {
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
                smsListDTO.setMessageType("english");
                String receivedMobileNumber;
                if (isProd) {
                    if (Objects.equals(applicantMobileNumber, "null") || applicantMobileNumber == null) {
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), spfcSmsRequestDTO, spfcMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
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
//                    log.info("sms service for applicant finova {}", finovaMsgDTOResponse);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), paisabuddySmsRequest, paisabuddyMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("cfl")) {

                log.info("in iffff");
                RequestDataDTO requestDataDTO = new RequestDataDTO();
//                if (paymentMode.equals("cash")) {
//                    requestDataDTO.setTemplateName("template3");
//                } else if (paymentMode.equals("cheque")) {
//                    requestDataDTO.setTemplateName("template2");
//                } else {
//                    requestDataDTO.setTemplateName("template1");
//                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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
                smsListDTO.setMessageType("english");
                String receivedMobileNumber;
                if (isProd) {
                    if (Objects.equals(applicantMobileNumber, "null") || applicantMobileNumber == null) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                    smsListDTO.setMobiles("91" + receivedMobileNumber);
                } else {
                    smsListDTO.setMobiles("91" + collectedFromMobileNumber);
//                    smsListDTO.setMobiles("919887432524");
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), cflSmsRequest, cflMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

            if (clientId.equals("lifc")) {

                log.info("in iffff");
                RequestDataDTO requestDataDTO = new RequestDataDTO();
//                if (paymentMode.equals("cash")) {
//                    requestDataDTO.setTemplateName("template3");
//                } else if (paymentMode.equals("cheque")) {
//                    requestDataDTO.setTemplateName("template2");
//                } else {
//                    requestDataDTO.setTemplateName("template1");
//                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String receiptDate = formatter.format(new Date());
                requestDataDTO.setTemplateName("template1");

                requestDataDTO.setMessageType("text");
                List<SmsListDTO> smsListDTOS = new ArrayList<>();
                List<String> strings = new ArrayList<>();
                strings.add(receiptAmount);
                strings.add(loanNumber);
                strings.add(receiptDate);
                strings.add(shortenUrlResponseDTO.getData().getResult());
                SmsListDTO smsListDTO = new SmsListDTO();
                smsListDTO.setMessageType("english");
                String receivedMobileNumber;
                if (isProd) {
                    if (Objects.equals(applicantMobileNumber, "null") || applicantMobileNumber == null) {
                        receivedMobileNumber = collectedFromMobileNumber;
                    } else {
                        receivedMobileNumber = applicantMobileNumber;
                    }
                    smsListDTO.setMobiles("91" + receivedMobileNumber);
                } else {
                    smsListDTO.setMobiles("91" + collectedFromMobileNumber);
//                    smsListDTO.setMobiles("919887432524");
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), lifcSmsRequest, lifcMsgDTOResponse, "success", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "");
            }

//            if(clientId.equals("deccan")) {
//                String paymentMode1 = Objects.equals(paymentMode, "cash") ? "Cash" : (Objects.equals(paymentMode, "upi") ? "Online" : "Cheque") ;
//                String message = DECCAN_TEMPLATE_MESSAGE.replace("{Var1}", receiptAmount);
//                message = message.replace("{paymentMode}", paymentMode1);
//                message = message.replace("{Var2}", loanNumber);
//                message = message.replace("{Var3}", shortenUrlResponseDTO.getData().getResult());
//                String receivedMobileNumber;
//                if(isProd) {
//                    receivedMobileNumber = applicantMobileNumber != null ? applicantMobileNumber : collectedFromMobileNumber; // uncomment this line and comment above static mobile number line while going live with CSL
//                } else {
//                    receivedMobileNumber = "9649916989";
//                }
//                log.info("message3 {}", message);
//                String encodedMessageString = URLEncoder.encode(message, StandardCharsets.UTF_8);
//                log.info("encodedMessageString {}", encodedMessageString);
//                String postField = "user=CSLFIN&message=" + encodedMessageString + "&key=974130e696XX&mobile=" + receivedMobileNumber + "&senderid=CSLSME&accusage=1&tempid=1707165942499421494&entityid=1701159697926729192&unicode=1";
//                log.info("postField {}", postField);
//                String smsServiceResponse = cslSmsService.sendSmsCsl(postField);
//                log.info("sms service for csl {}", smsServiceResponse);
//                saveSendSMSActivityData(loanId, res, userId);
//                // creating api logs
//                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), convertToJSON(postField), convertToJSON(smsServiceResponse), "success", Long.parseLong(loanId[0]));
//            }


        } catch (Exception ee) {
            log.info("ee messages", ee);
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), null, modifiedErrorMessage, "failure", Long.parseLong(loanId[0]), HttpMethod.POST.name(), "sendPdfOnCustomer");
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
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, UserDetailByUserIdDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getUserDetailsByUserId?userId=" + userId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDetailByUserIdDTOResponse.class)
                    .build().call();

//            log.info("responseData {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, res, "success", null, HttpMethod.GET.name(), "getUserDetailsByUserId?userId=" + userId);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getUserDetailsByUserId?userId=" + userId);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public Object getThermalPrintData(String receiptId) throws Exception {

        ThermalPrintDataDTO thermalPrintDataDTO = new ThermalPrintDataDTO();
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        String base64 = "";

        Map<String, Object> serviceRequestData = receiptRepository.getServiceRequestData(Long.parseLong(receiptId));
        if (serviceRequestData != null) {

            String dateTime = String.valueOf(serviceRequestData.get("created_date"));
            dateTime = dateTime.substring(0, dateTime.lastIndexOf("."));
            String[] splitDateTime = dateTime.split(" ");
            // date format change
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(splitDateTime[0]);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String newFormatDate = newDateFormat.format(date);

            String newDate = newFormatDate + " " + splitDateTime[1];

            String paymentMode = String.valueOf(serviceRequestData.get("payment_mode"));
            if (paymentMode.equals("upi")) {
                paymentMode = "UPI/NEFT";
            } else if (paymentMode.equals("cash")) {
                paymentMode = "Cash";
            } else if (paymentMode.equals("cheque")) {
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

            byte[] receiptBytes = printServiceImplementation.printDesign(thermalPrintDataDTO, currentUserInfo.getClientId());
            base64 = Base64.getEncoder().encodeToString(receiptBytes);
        } else {
            throw new Exception("1017002");
        }

        return new BaseDTOResponse<>(base64);
    }

    public String splitCodeName(String codeName) {
        String patternString = "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(codeName);
        while (matcher.find()) {
            String matchedSubstring = matcher.group(1);
            System.out.println("Matched: " + matchedSubstring);
            return matchedSubstring;
        }
        return codeName;
    }

    public String getTokenByApiKeySecret(Map<String, Object> map) throws Exception {
        String gateWayUrl ="https://api-" + (Objects.equals(springProfile, "pre-prod") ? "preprod" : Objects.equals(springProfile, "prod") ? "prod2" : springProfile) + ".synofin.tech/";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Source", String.valueOf(map.get("client")));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(String.valueOf(map.get("api_key")), String.valueOf(map.get("api_secret")));
        // deccantest credentials
//        httpHeaders.setBasicAuth("81c3d006fb5e4b64a0c3f2f595180081", "9fb0db7a-ac1c-4d7d-93fe-5b29a741c0af");

        String url = gateWayUrl + "oauth/authorization";
        MasterDTOResponse response = null;
        log.info("httpHeaders - {}", httpHeaders);
        log.info("url - {}", url);
        try {
            response = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST).httpHeaders(httpHeaders)
                    .url(url)
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call();
        } catch (Exception e) {
            log.error("error in generate token api", e);
            e.printStackTrace();
        }
        log.info("response - {}", response);
        AuthorizationResponse authorizationResponse = new ObjectMapper().convertValue(response.getData(), AuthorizationResponse.class);
        return authorizationResponse.getAuthData().getAccessToken();
    }

    @Override
    public BaseDTOResponse<Object> getDocuments(String token, String loanId) throws Exception {
        GetDocumentsResponseDTO res;
        List<Map<String, Object>> documentsDataArr = new ArrayList<>();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, GetDocumentsResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getDocuments?loanId=" + loanId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(GetDocumentsResponseDTO.class)
                    .build().call();

            log.info("res {}", res);
//            Map<String, Object> documentsData = new HashMap<>();

            if (res.getData().size() > 0) {
                for (GetDocumentsDataResponseDTO getDocumentsDataResponseDTO : res.getData()) {
                    Map<String, Object> documentsData = new HashMap<>();
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), "applicant") && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), "profile_photo")) {
                        documentsData.put("type", "applicant");
                        documentsData.put("document", getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), "coapplicant") && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), "profile_photo")) {
                        documentsData.put("type", "coapplicant");
                        documentsData.put("document", getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                    if (Objects.equals(getDocumentsDataResponseDTO.getApplicantType(), "guarantor") && Objects.equals(getDocumentsDataResponseDTO.getDocumentType(), "profile_photo")) {
                        documentsData.put("type", "guarantor");
                        documentsData.put("document", getDocumentsDataResponseDTO.getDocumentUrl());
                        documentsDataArr.add(documentsData);
                    }
                }
            }

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, res, "success", null, HttpMethod.GET.name(), "getDocuments?loanId=" + loanId);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getDocuments?loanId=" + loanId);
            log.error("{}", ee.getMessage());
        }

        return new BaseDTOResponse<>(documentsDataArr);
    }

    @Override
    public List<Map<String, Object>> formatDigitalSiteVisitData(List<Tuple> data) throws Exception {
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
            throw new RuntimeException(e);
        }
        return formattedRows;
    }

    @Override
    public BaseDTOResponse<Object> getCollaterals(Long loanIdNumber, String token) throws Exception {
        CollateralDetailsResponseDTO collateralResponse = new CollateralDetailsResponseDTO();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        try {
            collateralResponse = HTTPRequestService.<Object, CollateralDetailsResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCollaterals?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(CollateralDetailsResponseDTO.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, collateralResponse, "success", loanIdNumber, HttpMethod.GET.name(), "getCollaterals?loanId=" + loanIdNumber);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, modifiedErrorMessage, "failure", loanIdNumber, HttpMethod.GET.name(), "getCollaterals?loanId=" + loanIdNumber);
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(collateralResponse);
    }

    @Override
    public BaseDTOResponse<Object> employeeMobileNumberValidator(String token, String mobileNumber) throws Exception {
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
        return new BaseDTOResponse<Object>("No Match Found");
    }

    @Override
    public Object getBankAccountDetails(Long bankAccountId) {
        return receiptRepository.getBankAccountDetails(bankAccountId);
    }


    @Override
    public BaseDTOResponse<Object> checkTransactionReferenceNumber(String token, String transactionReferenceNumber) throws Exception {
        try {
            Map<String, Object> transactionNumberCheck = receiptRepository.transactionNumberCheck(transactionReferenceNumber);
            if (!transactionNumberCheck.isEmpty()) {
                return new BaseDTOResponse<Object>("UTR number already exist");
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<Object>("UTR number not found");
    }

    @Override
    public TaskDetailDTOResponse getChargesForLoan(String token, TaskDetailRequestDTO loanDataBody) throws Exception {
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
                    .build().call();

            log.info("loan details jhadsuhbsduh {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, loanRes, "success", Long.parseLong(loanDataBody.getRequestData().getLoanId()), HttpMethod.POST.name(), "getDataForLoanActions");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, modifiedErrorMessage, "failure", Long.parseLong(loanDataBody.getRequestData().getLoanId()), HttpMethod.GET.name(), "getDataForLoanActions");
            log.error("{}", e.getMessage());
        }
        return loanRes;
    }

    @Override
    public LoanBasicDetailsDTOResponse getBasicLoanDetails(String token, Long loanId) throws Exception {
        LoanBasicDetailsDTOResponse loanRes = new LoanBasicDetailsDTOResponse();
        try {
            loanRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(LoanBasicDetailsDTOResponse.class)
                    .build().call();

            log.info("loan details jhadsuhbsduh {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, loanRes, "success", loanId, HttpMethod.GET.name(), "getBasicLoanDetails?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, modifiedErrorMessage, "success", loanId, HttpMethod.GET.name(), "getBasicLoanDetails?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return loanRes;
    }

    @Override
    public CustomerDetailDTOResponse getCustomerDetails(String token, Long loanId) throws Exception {
        CustomerDetailDTOResponse customerRes = new CustomerDetailDTOResponse();
        try {
            customerRes = HTTPRequestService.<Object, CustomerDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCustomerDetails?loanId=" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(CustomerDetailDTOResponse.class)
                    .build().call();

            log.info("customerRes details {}", customerRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_customer_details, null, null, customerRes, "success", loanId, HttpMethod.GET.name(), "getCustomerDetails?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_customer_details, null, null, modifiedErrorMessage, "success", loanId, HttpMethod.GET.name(), "getCustomerDetails?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return customerRes;
    }

    @Override
    public LoanSummaryResponseDTO getLoanSummary(String token, Long loanId) throws Exception {
        LoanSummaryResponseDTO summaryRes = new LoanSummaryResponseDTO();
        try {
            summaryRes = HTTPRequestService.<Object, LoanSummaryResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getLoanSummaryForLoan/" + loanId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(LoanSummaryResponseDTO.class)
                    .build().call();

            log.info("summaryRes {}", summaryRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_loan_summary, null, null, summaryRes, "success", loanId, HttpMethod.GET.name(), "getLoanSummaryForLoan?loanId=" + loanId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_loan_summary, null, null, modifiedErrorMessage, "success", loanId, HttpMethod.GET.name(), "getLoanSummaryForLoan?loanId=" + loanId);
            log.error("{}", e.getMessage());
        }
        return summaryRes;
    }

    @Override
    public Object getCollectionIncentiveData(String token, CollectionIncentiveRequestDTOs collectionIncentiveRequestDTOs) throws Exception {

        String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
        String password = rsaUtils.getPassword(currentUserInfo.getClientId());
        Boolean piiPermission = true;
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> collectionDetails = receiptRepository.getCollectionIncentiveUsers();

        String dateWhereClause = " ";
        if(collectionIncentiveRequestDTOs.getStartDate() != null) {
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
            ;
//            List<Map<String, Object>> userLoanDetails = receiptRepository.getLoanDetailsOfUserForIncentive(String.valueOf(user.get("co_id")), encryptionKey, password, piiPermission, dateWhereClause);
            Map<String, Object> newUser = new HashMap<>(user);
            newUser.put("loan_details", this.formatDigitalSiteVisitData(userLoanDetails));
            response.add(newUser);
        }

        log.info("response {}", response);

        return response;
    }


    @Override
    public void createReceiptByCallBack(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity, String token, Map<String, Object> response, String utrNumber) throws Exception {
        log.info("Begin callback create receipt function");
        try {
            CurrentUserInfo currentUserInfo = new CurrentUserInfo();
            // implementing create receipt here
            ReceiptServiceDtoRequest receiptServiceDtoRequest = new ObjectMapper().convertValue(digitalPaymentTransactionsEntity.getReceiptRequestBody(), ReceiptServiceDtoRequest.class);
            receiptServiceDtoRequest.getRequestData().getRequestData().setTransactionReference(utrNumber);
            ServiceRequestSaveResponse resp = receiptService.createReceiptNew(receiptServiceDtoRequest, createBlankMultiPartFile(), createBlankMultiPartFile(), token, true);
            log.info("receipt response {}", resp);

            digitalPaymentTransactionsEntity.setReceiptResponse(resp.getData());
            if (resp.getData() != null && resp.getData().getServiceRequestId() != null) {
                log.info("in ifff receipt response {}", resp);
                response.put(RECEIPT_GENERATED, true);
                response.put(SR_ID, resp.getData().getServiceRequestId());
                digitalPaymentTransactionsEntity.setReceiptGenerated(true);
                String url = GET_PDF_API + resp.getData().getServiceRequestId();

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
                this.sendPdfToCustomerUsingS3(token, multipartFile, userRef, currentUserInfo.getClientId(), receiptServiceDtoRequest.getRequestData().getRequestData().getPaymentMode(), receiptServiceDtoRequest.getRequestData().getRequestData().getReceiptAmount(), updatedFileName,
                        receiptServiceDtoRequest.getActivityData().getUserId().toString(), receiptServiceDtoRequest.getCustomerType(),
                        receiptServiceDtoRequest.getCustomerName(), receiptServiceDtoRequest.getApplicantMobileNumber(), receiptServiceDtoRequest.getCollectedFromNumber(), receiptServiceDtoRequest.getLoanApplicationNumber(), resp.getData().getServiceRequestId());
                log.info("in callback create receipt function ending");
            }
        } catch (Exception e) {
            log.error("Error while create receipt via callback {}", e.getMessage());
            throw new Exception(e.getMessage());
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

            }
        };
    }

}
