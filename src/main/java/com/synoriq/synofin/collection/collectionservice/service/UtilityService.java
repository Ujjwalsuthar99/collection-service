package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeCallBackRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.sendOtpDTOs.SendOtpRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.verifyOtpDTOs.VerifyOtpRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeCheckStatusResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs.DynamicQrCodeResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface UtilityService {

    public Object getMasterData(String token, MasterDtoRequest requestBody) throws Exception;
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws Exception;
    public Object getContactSupport(String token,String keyword, String model) throws Exception;
    public Date addOneDay(Date date) throws Exception;
    public String mobileNumberMasking(String mobile);
    public String addSuffix(Integer i);
    public String capitalizeName(String name);
    public String getApiUrl();
    public String getApiUrl(String type);
    public Object getBankNameByIFSC(String keyword) throws Exception;
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token);
    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String module, String latitude, String longitude) throws IOException;
    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder, boolean isCustomerPhotos) throws Exception;
    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName, String userId, String customerType, String customerName, String applicantMobileNumber, String collectedFromMobileNumber, String loanNumber, Long receiptId) throws IOException;
    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId);
    public Object getThermalPrintData(String receiptId) throws Exception;
    public OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws Exception;
    public BaseDTOResponse<Object> getDocuments(String token, String loanId) throws Exception;
    public String convertToJSON(String input);
    public String splitCodeName(String input);

    public MasterDTOResponse sendOtp(String token, String mobileNumber) throws Exception;
    public MasterDTOResponse verifyOtp(String token, String mobileNumber, String otp) throws Exception;
    public MasterDTOResponse resendOtp(String token, String mobileNumber) throws Exception;
    public List<Map<String, Object>> formatDigitalSiteVisitData(List<Tuple> data) throws Exception;
    public BaseDTOResponse<Object> getCollaterals(Long loanIdNumber, String token) throws Exception;
    public BaseDTOResponse<Object> employeeMobileNumberValidator(String token, String mobileNumber) throws Exception;

}
