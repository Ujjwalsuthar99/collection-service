package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;


public interface UtilityService {

    public Object getMasterData(String token, MasterDtoRequest requestBody) throws Exception;
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws Exception;
    public Object getContactSupport(String token,String keyword, String model) throws Exception;
    public Date addOneDay(Date date) throws Exception;
    public String mobileNumberMasking(String mobile);
    public String addSuffix(Integer i);
    public Object getBankNameByIFSC(String keyword) throws Exception;
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token);
    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String userRefNo, String fileName) throws IOException;
    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder) throws IOException;
    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName, String userId, String customerType, String customerName, String applicantMobileNumber, String collectedFromMobileNumber, String loanNumber) throws IOException;
    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId);
    public Object getThermalPrintData(String receiptId) throws Exception;
    public OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws Exception;

}
