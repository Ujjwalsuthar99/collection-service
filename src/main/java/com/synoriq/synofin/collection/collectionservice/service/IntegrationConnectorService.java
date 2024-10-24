package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos.OcrCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ocrcheckresponsedtos.OcrCheckResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.DeleteImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.downloads3base64dtos.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IntegrationConnectorService {

    UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String module, GeoLocationDTO geoLocationDTO, String userName) throws CustomException, IOException;
    DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, boolean isNativeFolder, boolean isCustomerPhotos) throws CustomException;
    DeleteImageOnS3ResponseDTO deleteImageOnS3(String token, String userRefNo, String fileName) throws CustomException;
    MasterDTOResponse sendOtp(String token, String mobileNumber) throws CustomException;
    MasterDTOResponse verifyOtp(String token, String mobileNumber, String otp) throws CustomException;
    MasterDTOResponse resendOtp(String token, String mobileNumber) throws CustomException;
    OcrCheckResponseDTO ocrCheck(String token, OcrCheckRequestDTO requestBody) throws CustomException;

}
