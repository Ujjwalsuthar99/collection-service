package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.QRCodeVariables.AUTHORIZATION;
import static com.synoriq.synofin.collection.collectionservice.common.QRCodeVariables.CONTENTTYPE;


public interface UtilityService {

    public Object getMasterData(String token, MasterDtoRequest requestBody) throws Exception;
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws Exception;
    public Object getContactSupport(String token,String keyword, String model) throws Exception;
    public Date addOneDay(Date date) throws Exception;
    public String mobileNumberMasking(String mobile);
    public String addSuffix(Integer i);
    public String capitalizeName(String name);

    public static HttpHeaders createHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, token);
        httpHeaders.add(CONTENTTYPE, "application/json");
        return httpHeaders;
    }

    public String getApiUrl();
    public String getApiUrl(String type);
    public boolean isInteger(String str);
    public Object getBankNameByIFSC(String keyword) throws Exception;
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token);
    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName, String userId, String customerType, String customerName, String applicantMobileNumber, String collectedFromMobileNumber, String loanNumber, Long receiptId) throws IOException;
    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId);
    public Object getThermalPrintData(String receiptId) throws Exception;
    public BaseDTOResponse<Object> getDocuments(String token, String loanId) throws Exception;
    public String convertToJSON(String input);
    public String splitCodeName(String input);
    public String getTokenByApiKeySecret(Map<String, Object> map) throws Exception;
    public List<Map<String, Object>> formatDigitalSiteVisitData(List<Tuple> data) throws Exception;
    public BaseDTOResponse<Object> getCollaterals(Long loanIdNumber, String token) throws Exception;
    public BaseDTOResponse<Object> employeeMobileNumberValidator(String token, String mobileNumber) throws Exception;

}
