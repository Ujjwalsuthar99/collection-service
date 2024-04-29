package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.ocrCheckDTOs.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.DeleteImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IntegrationConnectorService {

    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String module, String latitude, String longitude, boolean multiReceiptUrl) throws IOException;
    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder, boolean isCustomerPhotos) throws Exception;
    public DeleteImageOnS3ResponseDTO deleteImageOnS3(String token, String userRefNo, String fileName) throws Exception;
    public MasterDTOResponse sendOtp(String token, String mobileNumber) throws Exception;
    public MasterDTOResponse verifyOtp(String token, String mobileNumber, String otp) throws Exception;
    public MasterDTOResponse resendOtp(String token, String mobileNumber) throws Exception;
    public OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws Exception;

}
