package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos.DeleteImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos.OcrCheckRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos.ResendOtpDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos.ResendOtpRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos.SendOtpDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos.SendOtpRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.verifyotpdtos.VerifyOtpDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.verifyotpdtos.VerifyOtpRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.DeleteImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.downloads3base64dtos.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ocrcheckresponsedtos.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.IntegrationConnectorService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Service
@Slf4j
public class IntegrationConnectorServiceImpl implements IntegrationConnectorService {

    private static final String FAILURE_STATUS = "failure";
    private static final String SUCCESS_STATUS = "success";
    private static final String UNKNOWN_STATUS = "unknown";
    private static final String[] URL_QUERY = {"&userRefNo=", "&isNativeFolder=", "&systemId="};

    private final UtilityService utilityService;

    private final CollectionConfigurationsRepository collectionConfigurationsRepository;

    private final ConsumedApiLogService consumedApiLogService;

    private final Random ran = new Random();


    public IntegrationConnectorServiceImpl(UtilityService utilityService,
                                           CollectionConfigurationsRepository collectionConfigurationsRepository,
                                           ConsumedApiLogService consumedApiLogService){
        this.utilityService = utilityService;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
        this.consumedApiLogService = consumedApiLogService;
    }

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String module, GeoLocationDTO geoLocationDTO, String userName) throws CustomException, IOException {
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();


        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(imageData.getBytes());

        String fileName = "";
        String userRefNo = "";

        String fileType = detectFileType(base64);
        if (base64.isEmpty()) {
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, null, res, FAILURE_STATUS, null, HttpMethod.POST.name(), "");
            IntegrationServiceErrorResponseDTO integrationServiceErrorResponseDTO = new IntegrationServiceErrorResponseDTO();
            integrationServiceErrorResponseDTO.setMessage("image base64 is empty");
            integrationServiceErrorResponseDTO.setCode("00000");
            res.setError(integrationServiceErrorResponseDTO);
            res.setData(null);
            return res;
        }

        if (fileType.equals(UNKNOWN_STATUS)) {
            throw new CustomException("1016056");
        }

        fileType = fileType.split("image/")[1];
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        int randomNumber = (100000 + ran.nextInt() * 900000);
        if (userName.isEmpty()) {
            userName = utilityService.getUserDetailsByToken(token).getData().getUserName();
        }
        switch (module) {
            case "follow_up":
                fileName = randomNumber + "_" + new Date().getTime() + "_" + "_followup_image." + fileType;
                userRefNo = "followUp/" + userName;
                break;
            case "create_receipt":
                fileName = randomNumber + "_" + new Date().getTime() + "_" + "_create_receipt_image." + fileType;
                userRefNo = "bankDepositSlip/" + userName;
                break;
            case "receipt_transfer":
                fileName = randomNumber + "_" + new Date().getTime() + "_" + "_deposit_image." + fileType;
                userRefNo = "depositSlip/" + userName;
                break;
            case "profile":
                fileName = "collection_" + currentUserInfo.getClientId().toLowerCase() + "_logo.png";
                userRefNo = "documents/logo";
                break;
            case "repossession_initiated_image":
            case "repossession_yard_image":
                fileName = randomNumber + "_" + new Date().getTime() + "_" + module + "." + fileType;
                userRefNo = "repossession/" + userName;
                break;
            default:
                fileName = "";
                userRefNo = "";
                break;
        }


        UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO = new UploadImageOnS3RequestDTO();
        UploadImageOnS3DataRequestDTO uploadImageOnS3DataRequestDTO = new UploadImageOnS3DataRequestDTO();
        uploadImageOnS3DataRequestDTO.setUserRefNo(userRefNo);
        uploadImageOnS3DataRequestDTO.setFileContentType("");
        uploadImageOnS3DataRequestDTO.setFileName(fileName);
        uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
        uploadImageOnS3RequestDTO.setSystemId(COLLECTION);
        uploadImageOnS3RequestDTO.setUserReferenceNumber("");
        uploadImageOnS3RequestDTO.setSpecificPartnerName("");
        log.info("uploadImageOnS3RequestDTO {}", uploadImageOnS3RequestDTO);

        try {

            String geoTaggingEnabled = collectionConfigurationsRepository.findConfigurationValueByConfigurationName("geo_tagging_enabled_on_photos");

            if (geoTaggingEnabled.equals("true") && (geoLocationDTO.getLatitude() != null) && (geoLocationDTO.getLongitude() != null)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = simpleDateFormat.format(new Date());

                InputStream inputStream = new ByteArrayInputStream(imageData.getBytes());
                BufferedImage image = ImageIO.read(inputStream);

                // Create a Graphics2D object from the BufferedImage object
                Graphics2D graphics2D = image.createGraphics();

                // Set the font and color for the watermark
                Font font = new Font("Arial", Font.BOLD, 42);
                String latLongWatermarkText = "lat: " + geoLocationDTO.getLatitude() + ", long: " + geoLocationDTO.getLongitude();
                String dateTimeWatermarkText = "Datetime: " + date;

                // Define margins and padding
                int topMargin = 20;
                int bottomMargin = 20;
                int padding = 10; // Padding between image borders and text

                // Calculate the maximum text width based on the image width and margins

                // Create a FontMetrics object to calculate text dimensions
                FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
                // Calculate the total text height

                int latitudeLongitudeX = (image.getWidth() - fontMetrics.stringWidth(latLongWatermarkText)) / 2;
                int latitudeLongitudeY = topMargin + fontMetrics.getHeight() + padding;

                // Draw the watermark onto the image
                graphics2D.setFont(font);
                graphics2D.setColor(Color.RED);
                graphics2D.drawString(latLongWatermarkText, latitudeLongitudeX, latitudeLongitudeY + fontMetrics.getAscent());


                // Calculate the position of the datetime text at the top center of the image
                int dateTimeX = (image.getWidth() - fontMetrics.stringWidth(dateTimeWatermarkText)) / 2;
                int dateTimeY = image.getHeight() - bottomMargin - fontMetrics.getHeight() - padding;

                graphics2D.drawString(dateTimeWatermarkText, dateTimeX, dateTimeY + fontMetrics.getAscent());

                // Save the updated image as a byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                byte[] updatedBytes = outputStream.toByteArray();

                base64 = encoder.encodeToString(updatedBytes);
            }

            if (base64.isEmpty()) {
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, null, res, FAILURE_STATUS, null, HttpMethod.POST.name(), "");
                IntegrationServiceErrorResponseDTO integrationServiceErrorResponseDTO = new IntegrationServiceErrorResponseDTO();
                integrationServiceErrorResponseDTO.setMessage("image base64 is empty");
                integrationServiceErrorResponseDTO.setCode("00000");
                res.setError(integrationServiceErrorResponseDTO);
                res.setData(null);
                return res;
            }
            uploadImageOnS3DataRequestDTO.setFile(base64);

            res = HTTPRequestService.<Object, UploadImageOnS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/uploadImageOnS3")
                    .body(uploadImageOnS3RequestDTO)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(UploadImageOnS3ResponseDTO.class)
                    .build().call(restTemplate);

            log.info("upload result {}", res);
            // setting userRefNo here
            if (res.getData() != null) {
                res.getData().setUserRefNo(userRefNo);
            }

            // creating api logs
            uploadImageOnS3DataRequestDTO.setFile("base64 string");
            uploadImageOnS3RequestDTO.setData(uploadImageOnS3DataRequestDTO);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, uploadImageOnS3RequestDTO, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "uploadImageOnS3");
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_upload, null, uploadImageOnS3RequestDTO, utilityService.convertToJSON(ee.getMessage()), FAILURE_STATUS, null, HttpMethod.POST.name(), "uploadImageOnS3");
            res.setData(null);
            throw new CustomException(ee.getMessage());
        }
        return res;
    }

    private String detectFileType(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        if (decodedBytes.length < 2) {
            return UNKNOWN_STATUS;
        }

        byte byte1 = decodedBytes[0];
        byte byte2 = decodedBytes[1];

        if ((byte1 & 0xFF) == 0xFF && (byte2 & 0xFF) == 0xD8) {
            return "image/jpeg";
        } else if ((byte1 & 0xFF) == 0x89 && (byte2 & 0xFF) == 0x50) {
            return "image/png";
        } else if ((byte1 & 0xFF) == 0x47 && (byte2 & 0xFF) == 0x49) {
            return "image/gif";
        } else if ((byte1 & 0xFF) == 0x42 && (byte2 & 0xFF) == 0x4D) {
            return "image/bmp";
        } else if ((byte1 & 0xFF) == 0x1F && (byte2 & 0xFF) == 0x8B) {
            return "application/gzip";
        } else if ((byte1 & 0xFF) == 0x50 && (byte2 & 0xFF) == 0x4B) {
            return "application/zip";
        } else {
            return UNKNOWN_STATUS;
        }
    }

    @Override
    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder, boolean isCustomerPhotos) throws CustomException {
        DownloadBase64FromS3ResponseDTO res = new DownloadBase64FromS3ResponseDTO();
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        String systemId = COLLECTION;
        if (isCustomerPhotos) {
            systemId = "collection_lms";
        }
        String requestBody = "getBase64ByFileName?fileName=" + fileName + URL_QUERY[0] + userRefNo + URL_QUERY[1] + isNativeFolder + URL_QUERY[2] + systemId;
        try {

            res = HTTPRequestService.<Object, DownloadBase64FromS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBase64ByFileName?fileName=" + fileName + URL_QUERY[0] + userRefNo + URL_QUERY[1] + isNativeFolder + URL_QUERY[2] + systemId)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(DownloadBase64FromS3ResponseDTO.class)
                    .build().call(restTemplate);

            String modifiedResponse = "response: " + res.getResponse() + " requestId: " + res.getRequestId() + " error: " + res.getError();
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_download, null, null, utilityService.convertToJSON(modifiedResponse), SUCCESS_STATUS, null, HttpMethod.GET.name(), "getBase64ByFileName?fileName=" + fileName + URL_QUERY[0] + userRefNo + URL_QUERY[1] + isNativeFolder + URL_QUERY[2] + systemId);
            // again calling the download api for aadharfin usernames
            if ((res.getData().isEmpty() || res.getData().contains("File or bucket not")) && Objects.equals(currentUserInfo.getClientId(), "aadharfin")) {
                log.info("Again calling the download API for Aadharfin Client");
                String[] userRefArr = userRefNo.split("/");
                String newUserRef = userRefArr[0] + "/" + userRefArr[1].substring(0, 1).toUpperCase() + userRefArr[1].substring(1);

                res = HTTPRequestService.<Object, DownloadBase64FromS3ResponseDTO>builder()
                        .httpMethod(HttpMethod.GET)
                        .url("http://localhost:1102/v1/getBase64ByFileName?fileName=" + fileName + URL_QUERY[0] + newUserRef + URL_QUERY[1] + isNativeFolder + URL_QUERY[2] + systemId)
                        .httpHeaders(UtilityService.createHeaders(token))
                        .typeResponseType(DownloadBase64FromS3ResponseDTO.class)
                        .build().call(restTemplate);
            }

        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_download, null, requestBody, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.GET.name(), "s3Download");
            log.error("juuju{}", ee.getMessage());
            res.setResponse(false);
            res.setData(null);
            res.setErrorFields(ee.getMessage());
            throw new CustomException(ee.getMessage());

        }
        return res;
    }

    @Override
    public DeleteImageOnS3ResponseDTO deleteImageOnS3(String token, String userRefNo, String fileName) throws CustomException {
        DeleteImageOnS3ResponseDTO result = new DeleteImageOnS3ResponseDTO();
        DeleteImageOnS3RequestDTO deleteImageOnS3RequestDTO = new DeleteImageOnS3RequestDTO();
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("reference_path", userRefNo);
        dataRequest.put("file_name", fileName);
        deleteImageOnS3RequestDTO.setData(dataRequest);
        deleteImageOnS3RequestDTO.setSystemId(COLLECTION);
        deleteImageOnS3RequestDTO.setSpecificPartnerName("");
        deleteImageOnS3RequestDTO.setUserReferenceNumber("");

        try {
            result = HTTPRequestService.<Object, DeleteImageOnS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/deleteImageOnS3")
                    .body(deleteImageOnS3RequestDTO)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(DeleteImageOnS3ResponseDTO.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_delete, null, deleteImageOnS3RequestDTO, result, SUCCESS_STATUS, null, HttpMethod.POST.name(), "deleteImageOnS3");

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.s3_delete, null, deleteImageOnS3RequestDTO, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "deleteImageOnS3");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return result;

    }

    @Override
    public MasterDTOResponse sendOtp(String token, String mobileNumber) throws CustomException {
        MasterDTOResponse res = new MasterDTOResponse();

        SendOtpRequestDTO sendOtpRequestDTO = new SendOtpRequestDTO();
        SendOtpDataRequestDTO sendOtpDataRequestDTO = new SendOtpDataRequestDTO();
        sendOtpDataRequestDTO.setOtpExpiry("10");
        sendOtpDataRequestDTO.setSpecificOtp("");
        sendOtpDataRequestDTO.setOtpCodeLength(6);
        sendOtpDataRequestDTO.setTemplateName("template1");
        sendOtpDataRequestDTO.setPhoneNumber("91" + mobileNumber);
        sendOtpDataRequestDTO.setTemplateVariable(new ArrayList<>());
        sendOtpRequestDTO.setSystemId(COLLECTION);
        sendOtpRequestDTO.setData(sendOtpDataRequestDTO);

        try {

            res = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/send-otp")
                    .body(sendOtpRequestDTO)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_otp, null, sendOtpRequestDTO, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "send_otp");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.send_otp, null, sendOtpRequestDTO, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "send_otp");
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }
        return res;
    }

    @Override
    public MasterDTOResponse verifyOtp(String token, String mobileNumber, String otp) throws CustomException {
        MasterDTOResponse res = new MasterDTOResponse();

        VerifyOtpRequestDTO verifyOtpRequestDTO = new VerifyOtpRequestDTO();
        VerifyOtpDataRequestDTO verifyOtpDataRequestDTO = new VerifyOtpDataRequestDTO();
        verifyOtpDataRequestDTO.setOtp(otp);
        verifyOtpDataRequestDTO.setId("91" + mobileNumber);
        verifyOtpRequestDTO.setSystemId(COLLECTION);
        verifyOtpRequestDTO.setData(verifyOtpDataRequestDTO);
        try {

            res = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/verify-otp")
                    .body(verifyOtpRequestDTO)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.verify_otp, null, verifyOtpRequestDTO, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "verifyOtp");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.verify_otp, null, verifyOtpRequestDTO, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "verifyOtp");
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }
        return res;
    }

    @Override
    public MasterDTOResponse resendOtp(String token, String mobileNumber) throws CustomException {
        MasterDTOResponse res = new MasterDTOResponse();

        ResendOtpRequestDTO resendOtpRequestDTO = new ResendOtpRequestDTO();
        ResendOtpDataRequestDTO resendOtpDataRequestDTO = new ResendOtpDataRequestDTO();
        resendOtpDataRequestDTO.setRetryType("text");
        resendOtpDataRequestDTO.setPhoneNumber("91" + mobileNumber);
        resendOtpRequestDTO.setSystemId(COLLECTION);
        resendOtpRequestDTO.setData(resendOtpDataRequestDTO);
        try {

            res = HTTPRequestService.<Object, MasterDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/resend-otp")
                    .body(resendOtpRequestDTO)
                    .httpHeaders(UtilityService.createHeaders(token))
                    .typeResponseType(MasterDTOResponse.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.resend_otp, null, resendOtpRequestDTO, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "resendOtp");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.resend_otp, null, resendOtpRequestDTO, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "resendOtp");
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }
        return res;
    }

    @Override
    public OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws CustomException {
        OcrCheckResponseDTO res = new OcrCheckResponseDTO();
        String base64 = requestBody.getData().getImgBaseUrl();
        base64 = base64.replace("\n", "");
        OcrCheckRequestDataDTO ocrCheckRequestDataDTO = new OcrCheckRequestDataDTO();
        ocrCheckRequestDataDTO.setImgBaseUrl(base64);
        ocrCheckRequestDataDTO.setImgType(requestBody.getData().getImgType());
        requestBody.setData(ocrCheckRequestDataDTO);
        try {
            OcrCheckRequestDTO ocrCheckBody = new ObjectMapper().convertValue(requestBody, OcrCheckRequestDTO.class);

            log.info("ocrCheckBody {}", ocrCheckBody);

            res = HTTPRequestService.<Object, OcrCheckResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/ocrCheck")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(ocrCheckBody)
                    .typeResponseType(OcrCheckResponseDTO.class)
                    .build().call(restTemplate);

            log.info("res {}", res);
            ocrCheckRequestDataDTO.setImgBaseUrl("base64 string");
            requestBody.setData(ocrCheckRequestDataDTO);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.cheque_ocr, null, requestBody, res, SUCCESS_STATUS, null, HttpMethod.POST.name(), "ocrCheck");
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.cheque_ocr, null, requestBody, modifiedErrorMessage, FAILURE_STATUS, null, HttpMethod.POST.name(), "ocrCheck");
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }

        return res;
    }


}
