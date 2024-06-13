package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CustomerDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanBasicDetailsDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.TaskDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import java.io.IOException;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.AUTHORIZATION;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.CONTENTTYPE;


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
        if (token.contains("Bearer")) {
            httpHeaders.add(AUTHORIZATION, token);
        } else {
            httpHeaders.setBearerAuth(token);
        }
        httpHeaders.add(CONTENTTYPE, "application/json");
        return httpHeaders;
    }

    public String getApiUrl();
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
    public BaseDTOResponse<Object> checkTransactionReferenceNumber(String token, String transactionReferenceNumber) throws Exception;

    default HttpEntity<byte[]> prepareMultipartFile(MultipartFile documentFile) throws Exception {
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("transfer_proof")
                .filename(Objects.requireNonNull(documentFile.getOriginalFilename()))
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        return new HttpEntity<>(documentFile.getBytes(), fileMap);
    }
    @NotNull
    static HashMap<String, Object> getStringObjectMapCopy(UploadImageOnS3ResponseDTO uploadedImage) throws ConnectorException {
        if (uploadedImage.getData() == null)
            throw new ConnectorException(uploadedImage.getError(), HttpStatus.FAILED_DEPENDENCY, uploadedImage.getRequestId());

        // creating images Object
        HashMap<String, Object> imageMap = new HashMap<>();
        int i = 1;
        if (uploadedImage.getData().getFileName() != null) {
            imageMap.put("url" + i, uploadedImage.getData().getFileName());
        }
        return imageMap;
    }
    Object getBankAccountDetails(Long bankAccountId);

    TaskDetailDTOResponse getChargesForLoan(String token, TaskDetailRequestDTO loanDataBody) throws Exception;

    LoanBasicDetailsDTOResponse getBasicLoanDetails(String token, Long loanId) throws Exception;

    CustomerDetailDTOResponse getCustomerDetails(String token, Long loanId) throws Exception;

    LoanSummaryResponseDTO getLoanSummary(String token, Long loanId) throws Exception;

    public Object getCollectionIncentiveData(String token, String startDate, String endDate) throws Exception;
}
