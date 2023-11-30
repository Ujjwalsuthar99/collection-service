package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs.GetDocumentsDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs.GetDocumentsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.FinovaMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.PaisabuddyMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.SpfcMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ShortenUrlDTOs.ShortenUrlResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDataDTOs.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.BankNameIFSCResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.ContactResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.CslSmsService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.FinovaSmsService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.PaisabuddySmsService;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.SpfcSmsService;
import com.synoriq.synofin.collection.collectionservice.service.printService.PrintServiceImplementation;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.USER_MESSAGE;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

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
    private PrintServiceImplementation printServiceImplementation;

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    CslSmsService cslSmsService;

    @Autowired
    private SpfcSmsService spfcSmsService;

    @Autowired
    private PaisabuddySmsService paisabuddySmsService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;

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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, masterBody, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_master_type, null, requestBody, modifiedErrorMessage, "failure", null);
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, convertToJSON(modifiedResponse), "success", null);
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
                    pageableArr.add(filteredList.get(i));
                }
                baseDTOResponse = new BaseDTOResponse<>(pageableArr);
            }

        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.fetch_all_user_data, null, null, modifiedErrorMessage, "failure", null);

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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.contact_support, null, null, modifiedErrorMessage, "failure", null);
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

    ;

    @Override
    public String getApiUrl() {
        if (Objects.equals(springProfile, "pre-prod")) {
            springProfile = "preprod";
        }
        String queryString = httpServletRequest.getQueryString();
//        httpServletRequest.getRequestURI() = "/collection-service/v1/getMasterType";
        log.info("queryString {}", queryString);
        if (queryString != null) {
            return "https://api-" + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI() + "?" + queryString;
        }
        return "https://api-" + springProfile + ".synofin.tech" + httpServletRequest.getRequestURI();
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.razor_pay_ifsc, 0L, null, modifiedErrorMessage, "failure", null);
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_token_details, null, null, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String userRefNo, String fileName, String latitude, String longitude) throws IOException {
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();


        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(imageData.getBytes());

        UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO = new UploadImageOnS3RequestDTO();
        UploadImageOnS3DataRequestDTO uploadImageOnS3DataRequestDTO = new UploadImageOnS3DataRequestDTO();
        uploadImageOnS3DataRequestDTO.setUserRefNo(userRefNo);
        uploadImageOnS3DataRequestDTO.setFileContentType("");
        uploadImageOnS3DataRequestDTO.setFileName(fileName);
        uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
        uploadImageOnS3RequestDTO.setSystemId("collection");
        uploadImageOnS3RequestDTO.setUserReferenceNumber("");
        uploadImageOnS3RequestDTO.setSpecificPartnerName("");


        try {

            String geoTaggingEnabled = collectionConfigurationsRepository.findConfigurationValueByConfigurationName("geo_tagging_enabled_on_photos");

            if (geoTaggingEnabled.equals("true")) {
                if ((latitude != null) && (longitude != null)) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    // Create a BufferedImage object from the byte array
                    InputStream inputStream = new ByteArrayInputStream(imageData.getBytes());
                    BufferedImage image = ImageIO.read(inputStream);

                    // Create a Graphics2D object from the BufferedImage object
                    Graphics2D graphics2D = image.createGraphics();

                    // Set the font and color for the watermark
                    Font font = new Font("Arial", Font.BOLD, 50);
                    String watermarkText = "Latitude: " + latitude + ", Longitude: " + longitude + ", Datetime:" + now;

                    // Define margins and padding
                    int leftMargin = 20;
                    int rightMargin = 20;
                    int topMargin = 20;
                    int bottomMargin = 20;
                    int padding = 10; // Padding between image borders and text

                    // Calculate the maximum text width based on the image width and margins
                    int maxTextWidth = image.getWidth() - leftMargin - rightMargin;

                    // Create a FontMetrics object to calculate text dimensions
                    FontMetrics fontMetrics = graphics2D.getFontMetrics(font);

                    // Split the watermarkText into words
                    String[] words = watermarkText.split("\\s+");
                    StringBuilder currentLine = new StringBuilder();
                    ArrayList<String> lines = new ArrayList<>();

                    for (String word : words) {
                        // Check if adding the next word exceeds the max text width
                        if (fontMetrics.stringWidth(currentLine.toString() + " " + word) > maxTextWidth - 2 * padding) {
                            lines.add(currentLine.toString().trim());
                            currentLine = new StringBuilder();
                        }
                        currentLine.append(word).append(" ");
                    }
                    // Add the last line
                    lines.add(currentLine.toString().trim());

                    // Calculate the total text height
                    int totalTextHeight = lines.size() * (fontMetrics.getHeight() + 2 * padding);

                    // Calculate the position of the watermark on the bottom center of the image
                    int x = (image.getWidth() - maxTextWidth) / 2;
                    int y = image.getHeight() - totalTextHeight - bottomMargin;

                    // Draw the watermark onto the image
                    graphics2D.setFont(font);
                    graphics2D.setColor(Color.RED);
                    for (String line : lines) {
                        int textWidth = fontMetrics.stringWidth(line);
                        int textX = x + (maxTextWidth - textWidth) / 2;
                        graphics2D.drawString(line, textX, y + padding + fontMetrics.getAscent());
                        y += fontMetrics.getHeight() + 2 * padding;
                    }

                    // Save the updated image as a byte array
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", outputStream);
                    byte[] updatedBytes = outputStream.toByteArray();

                    base64 = encoder.encodeToString(updatedBytes);
                }
            }



            uploadImageOnS3DataRequestDTO.setFile(base64);

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

            log.info("upload result {}", res);
            // creating api logs
            uploadImageOnS3DataRequestDTO.setFile("base64 string");
            uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, uploadImageOnS3RequestDTO, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, uploadImageOnS3RequestDTO, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    @Override
    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder, boolean isCustomerPhotos) throws Exception {
        DownloadBase64FromS3ResponseDTO res = new DownloadBase64FromS3ResponseDTO();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            String systemId = "collection";
            if (isCustomerPhotos) {
                systemId = "collection_lms";
            }

            res = HTTPRequestService.<Object, DownloadBase64FromS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBase64ByFileName?fileName=" + fileName + "&userRefNo=" + userRefNo + "&isNativeFolder=" + isNativeFolder + "&systemId=" + systemId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(DownloadBase64FromS3ResponseDTO.class)
                    .build().call();

            String modifiedResponse = "response: " + res.getResponse() + " requestId: " + res.getRequestId() + " error: " + res.getError();
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_download, null, null, convertToJSON(modifiedResponse), "success", null);

        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_download, null, null, modifiedErrorMessage, "failure", null);
            log.error("juuju{}", ee.getMessage());
            res.setResponse(false);
            res.setData(null);
            res.setErrorFields(ee.getMessage());

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
        uploadImageOnS3RequestDTO.setSystemId("collection");
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, Long.parseLong(userId), uploadImageOnS3RequestDTO, res, "success", Long.parseLong(loanId[0]));


            ShortenUrlResponseDTO shortenUrlResponseDTO;
            ShortenUrlRequestDTO shortenUrlRequestDTO = new ShortenUrlRequestDTO();
            ShortenUrlDataRequestDTO shortenUrlDataRequestDTO = new ShortenUrlDataRequestDTO();

            shortenUrlRequestDTO.setUserReferenceNumber("");
            shortenUrlRequestDTO.setSpecificPartnerName("");
            shortenUrlRequestDTO.setSystemId("collection");
            shortenUrlDataRequestDTO.setId(res.getData().getDownloadUrl());
            shortenUrlRequestDTO.setData(shortenUrlDataRequestDTO);
            String shortenUrl = "";
            if (springProfile.equals("pre-prod")) {
                shortenUrl = SHORTEN_URL_PREPROD;
            } else {
                shortenUrl = SHORTEN_URL_PREPROD.replace("preprod", springProfile);
            }

            shortenUrlResponseDTO = HTTPRequestService.<Object, ShortenUrlResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(shortenUrl)
                    .body(shortenUrlRequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ShortenUrlResponseDTO.class)
                    .build().call();

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.shorten_url, Long.parseLong(userId), shortenUrlRequestDTO, shortenUrlResponseDTO, "success", Long.parseLong(loanId[0]));


            if (clientId.equals("finova")) {
                FinovaSmsRequest finovaSmsRequest = new FinovaSmsRequest();
                if (paymentMode.equals("cash")) {
                    finovaSmsRequest.setTemplateId(FINOVA_CASH_MSG_FLOW_ID);
                } else if (paymentMode.equals("cheque")) {
                    finovaSmsRequest.setTemplateId(FINOVA_CHEQUE_MSG_FLOW_ID);
                } else {
                    finovaSmsRequest.setTemplateId(FINOVA_UPI_MSG_FLOW_ID);
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
                    finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                    FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
                    saveSendSMSActivityData(loanId, res, userId);
//                    log.info("sms service for applicant finova {}", finovaMsgDTOResponse);
                    // creating api logs
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, "success", Long.parseLong(loanId[0]));

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
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), finovaSmsRequest, finovaMsgDTOResponse, "success", Long.parseLong(loanId[0]));
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), convertToJSON((postField + postData)), convertToJSON(smsServiceResponse), "success", Long.parseLong(loanId[0]));
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
                spfcSmsRequestDTO.setSystemId("collection");
                spfcSmsRequestDTO.setUserReferenceNumber("");
                spfcSmsRequestDTO.setSpecificPartnerName("");
                spfcSmsRequestDTO.setData(requestDataDTO);

                SpfcMsgDTOResponse spfcMsgDTOResponse = spfcSmsService.sendSmsSpfc(spfcSmsRequestDTO, token, springProfile);
                saveSendSMSActivityData(loanId, res, userId);
                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), spfcSmsRequestDTO, spfcMsgDTOResponse, "success", Long.parseLong(loanId[0]));
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
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), paisabuddySmsRequest, paisabuddyMsgDTOResponse, "success", Long.parseLong(loanId[0]));
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.sms_service, Long.parseLong(userId), null, modifiedErrorMessage, "failure", Long.parseLong(loanId[0]));
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_user_details_admin, userId, null, modifiedErrorMessage, "failure", null);
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

    @Override
    public OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws Exception {
        OcrCheckResponseDTO res = new OcrCheckResponseDTO();
        String base64 = requestBody.getData().getImgBaseUrl();
        base64 = base64.replace("\n", "");
        OcrCheckRequestDataDTO ocrCheckRequestDataDTO = new OcrCheckRequestDataDTO();
        ocrCheckRequestDataDTO.setImgBaseUrl(base64);
        ocrCheckRequestDataDTO.setImgType(requestBody.getData().getImgType());
        requestBody.setData(ocrCheckRequestDataDTO);
        try {
            OcrCheckRequestDTO ocrCheckBody = new ObjectMapper().convertValue(requestBody, OcrCheckRequestDTO.class);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");
            log.info("ocrCheckBody {}", ocrCheckBody);

            res = HTTPRequestService.<Object, OcrCheckResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/ocrCheck")
                    .httpHeaders(httpHeaders)
                    .body(ocrCheckBody)
                    .typeResponseType(OcrCheckResponseDTO.class)
                    .build().call();

            log.info("res {}", res);
            ocrCheckRequestDataDTO.setImgBaseUrl("base64 string");
            requestBody.setData(ocrCheckRequestDataDTO);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.cheque_ocr, null, requestBody, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.cheque_ocr, null, requestBody, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }

        return res;
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_documents, null, null, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }

        return new BaseDTOResponse<>(documentsDataArr);
    }

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
        if(requestBody.getVendor().equals("kotak")) {
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

            if(res.getResponse().equals(true)) {

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
                digitalPaymentTransactionsEntity.setMobileNo(null);
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
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_qr_code, null, integrationRequestBody, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public DynamicQrCodeCheckStatusResponseDTO getQrCodeTransactionStatus(String token, DynamicQrCodeStatusCheckRequestDTO requestBody) throws Exception {
        DynamicQrCodeCheckStatusResponseDTO res = new DynamicQrCodeCheckStatusResponseDTO();
        DynamicQrCodeStatusCheckIntegrationRequestDTO dynamicQrCodeStatusCheckIntegrationRequestDTO = new DynamicQrCodeStatusCheckIntegrationRequestDTO();
        DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO = new DynamicQrCodeStatusCheckDataRequestDTO();

        dynamicQrCodeStatusCheckDataRequestDTO.setMerchantTranId(requestBody.getMerchantTranId());
        dynamicQrCodeStatusCheckDataRequestDTO.setCustomerId("91" + requestBody.getMobileNumber());

        dynamicQrCodeStatusCheckIntegrationRequestDTO.setDynamicQrCodeStatusCheckDataRequestDTO(dynamicQrCodeStatusCheckDataRequestDTO);
        dynamicQrCodeStatusCheckIntegrationRequestDTO.setUserReferenceNumber(String.valueOf(requestBody.getUserId()));
        dynamicQrCodeStatusCheckIntegrationRequestDTO.setSystemId("collection");
        dynamicQrCodeStatusCheckIntegrationRequestDTO.setSpecificPartnerName(requestBody.getVendor());
        try {
//            String isCheckQrPaymentStatusAvailableConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(IS_QR_CODE_CHECK_PAYMENT_STATUS_AVAILABLE);
//
//            String[] isCheckQrPaymentStatusAvailable  = isCheckQrPaymentStatusAvailableConf.split("/");

//            if(Arrays.asList(isCheckQrPaymentStatusAvailable).contains(requestBody.getVendor())) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsId(requestBody.getDigitalPaymentTransactionId());

            if(digitalPaymentTransactionsEntity.getStatus().equals("success")) {
                throw new Exception("1016045");
            }

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
            collectionActivityLogsEntity.setActivityBy(requestBody.getUserId());
            collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
            collectionActivityLogsEntity.setAddress("{}");
            collectionActivityLogsEntity.setRemarks("The payment status for transaction id " + requestBody.getDigitalPaymentTransactionId() + " and loan id " + requestBody.getLoanId() + " has been updated as success by checking the status manually");
            collectionActivityLogsEntity.setImages("{}");
            collectionActivityLogsEntity.setLoanId(requestBody.getLoanId());
            collectionActivityLogsEntity.setGeolocation("{}");

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            digitalPaymentTransactionsEntity.setUtrNumber(res.getData().getOriginalBankRRN());
            digitalPaymentTransactionsEntity.setStatus(res.getData().getStatus().toLowerCase());
            digitalPaymentTransactionsEntity.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());

            digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
//            } else {
//                res.setResponse(true);
//                Map<String, Object> digitalPaymentTransaction = digitalPaymentTransactionsRepository.findByDigitalPaymentTransactionsIdForCheckStatusResponse(requestBody.getDigitalPaymentTransactionId(), requestBody.getMerchantTranId());
//                DynamicQrCodeCheckStatusDataResponseDTO dynamicQrCodeCheckStatusDataResponseDTO = new DynamicQrCodeCheckStatusDataResponseDTO();
//                dynamicQrCodeCheckStatusDataResponseDTO.setMerchantTranId(String.valueOf(digitalPaymentTransaction.get("merchantTransId")));
//                dynamicQrCodeCheckStatusDataResponseDTO.setStatus(String.valueOf(digitalPaymentTransaction.get("status")));
//                dynamicQrCodeCheckStatusDataResponseDTO.setAmount(String.valueOf(digitalPaymentTransaction.get("amount")));
//                res.setData(dynamicQrCodeCheckStatusDataResponseDTO);
//            }



            log.info("res {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, requestBody, res, "success", null);
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.check_qr_payment_status, null, requestBody, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
        }

        return res;
    }

    @Override
    public Object qrCodeCallBack(String token, DynamicQrCodeCallBackRequestDTO requestBody) throws Exception {
        String merchantTransId = requestBody.getMerchantTranId();
        Map<String, Object> response = new HashMap<>();
        try {
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = digitalPaymentTransactionsRepository.findByMerchantTranId(merchantTransId);
            if (digitalPaymentTransactionsEntity != null) {
                if (Objects.equals(requestBody.getStatus(), "SUCCESS")) {
                    digitalPaymentTransactionsEntity.setStatus("success");
                }
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
                response.put("status", true);
            } else {
                response.put("status", false);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("errorMessage {}", errorMessage);
            throw new Exception();
        }
        return response;
    }
}
