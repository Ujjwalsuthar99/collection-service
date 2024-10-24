package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.common.exception.DataLockException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.collectionincentivedtos.CollectionIncentiveRequestDTOs;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterdtos.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.CustomerDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.LoanBasicDetailsDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.loansummaryforloandtos.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.TaskDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdetailbytokendtos.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdetailsbyuseriddtos.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3imagedtos.uploadimageresponsedto.UploadImageOnS3ResponseDTO;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.AUTHORIZATION;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.CONTENTTYPE;


public interface UtilityService {

    public Object getMasterData(String token, MasterDtoRequest requestBody) throws CustomException;
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws CustomException;
    public Object getContactSupport(String token,String keyword, String model) throws CustomException;
    public Date addOneDay(Date date) throws CustomException;
    public String mobileNumberMasking(String mobile);
    public String addSuffix(Integer i);
    public String capitalizeName(String name);

    static HttpHeaders createHeaders(String token) {
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
    public Object getBankNameByIFSC(String keyword) throws CustomException;
    public UserDetailByTokenDTOResponse getUserDetailsByToken(String token);
    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName, String userId, String customerType, String customerName, String applicantMobileNumber, String collectedFromMobileNumber, String loanNumber, Long receiptId) throws IOException;
    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId);
    public Object getThermalPrintData(String receiptId) throws CollectionException;
    public BaseDTOResponse<Object> getDocuments(String token, String loanId) throws CustomException;
    public String convertToJSON(Object input);
    public String splitCodeName(String input);
    public String getTokenByApiKeySecret(Map<String, Object> map) throws CustomException;
    public List<Map<String, Object>> formatDigitalSiteVisitData(List<Tuple> data) throws CustomException;
    public BaseDTOResponse<Object> getCollaterals(Long loanIdNumber, String token) throws CustomException;
    public BaseDTOResponse<Object> employeeMobileNumberValidator(String token, String mobileNumber) throws CustomException;
    public BaseDTOResponse<Object> checkTransactionReferenceNumber(String token, String transactionReferenceNumber) throws CustomException;

    default HttpEntity<byte[]> prepareMultipartFile(MultipartFile documentFile) throws CustomException {
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("transfer_proof")
                .filename(Objects.requireNonNull(documentFile.getOriginalFilename()))
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        try {
            return new HttpEntity<>(documentFile.getBytes(), fileMap);
        } catch (IOException e) {
            throw new CustomException(e.getMessage());
        }
    }

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

    TaskDetailDTOResponse getChargesForLoan(String token, TaskDetailRequestDTO loanDataBody) throws CustomException;

    LoanBasicDetailsDTOResponse getBasicLoanDetails(String token, Long loanId) throws CustomException;

    CustomerDetailDTOResponse getCustomerDetails(String token, Long loanId) throws CustomException;

    LoanSummaryResponseDTO getLoanSummary(String token, Long loanId) throws CustomException;

    Object getCollectionIncentiveData(String token, CollectionIncentiveRequestDTOs collectionIncentiveRequestDTOs) throws CustomException;

    default boolean isExpired(int minute, Date date, boolean afterDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        if (afterDate)
            return new Date().after(cal.getTime());
        else
            return new Date().before(cal.getTime());
    }

    default String addMinutes(int minute, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return simpleDateFormat.format(cal.getTime());
    }

    default CollectionActivityLogsEntity getCollectionActivityLogsEntity(String activityName, Long userId, Long loanId, String remarks, Object geoLocation, Long batteryPercentage) {
        CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
        collectionActivityLogsEntity.setActivityName(activityName);
        collectionActivityLogsEntity.setActivityDate(new Date());
        collectionActivityLogsEntity.setDeleted(false);
        collectionActivityLogsEntity.setActivityBy(userId);
        collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
        collectionActivityLogsEntity.setAddress("{}");
        collectionActivityLogsEntity.setRemarks(remarks);
        collectionActivityLogsEntity.setImages("{}");
        collectionActivityLogsEntity.setLoanId(loanId);
        collectionActivityLogsEntity.setGeolocation(geoLocation);
        collectionActivityLogsEntity.setBatteryPercentage(batteryPercentage);
        return collectionActivityLogsEntity;
    }
    void createReceiptByCallBack(DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity, String token, Map<String, Object> mainResponse, String utrNumber) throws CustomException, DataLockException, InterruptedException, IOException;
}
