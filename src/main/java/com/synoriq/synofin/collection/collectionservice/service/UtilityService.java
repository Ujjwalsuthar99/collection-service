package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.FinovaSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageData;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs.DownloadBase64FromS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgServiceResponse.FinovaMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.shortenUrl.ShortenUrlResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.msgservice.FinovaSmsService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UtilityService {
    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Autowired
    FinovaSmsService finovaSmsService;

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;

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

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }

        return res;
    }
    public Object getUserDetail(String token, Integer page, Integer size, String key) throws Exception {

        UserDTOResponse res = new UserDTOResponse();
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

            List<UsersDataDTO> userData = res.getData();
            for (int i = 0; i < userData.toArray().length; i++) {
                userData.get(i).setTransferTo(userData.get(i).getName() + " - " + userData.get(i).getEmployeeCode());
            }
            log.info("userData.toArray().length {}", userData.toArray().length);
            int pageRequest = (page * size) - 10;
            List<UsersDataDTO> pageableArr = new LinkedList<>();

//            List<UsersDataDTO> filteredList = userData.parallelStream().filter(user -> (user.getUsername().contains(key) || user.getName().contains(key))).collect(Collectors.toList());
            if (key.equals("")) {
                for (int i = pageRequest; i < (pageRequest+10); i++) {
                    pageableArr.add(userData.get(i));
                }
                baseDTOResponse = new BaseDTOResponse<>(pageableArr);
            } else {
                List<UsersDataDTO> filteredList = userData.
                                                stream().
                                                filter(user -> (user.getUsername() != null && user.getName() != null && user.getEmployeeCode() != null) && (Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getUsername()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getName()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getEmployeeCode()).find())).
                                                collect(Collectors.toList());
                log.info("filteredList {}", filteredList);
                int length;
                int filterSize = filteredList.size();
                filterSize = filterSize - pageRequest ;
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

            log.info("pageableArr {}", pageableArr);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }

        return baseDTOResponse;
    }
    public Object getContactSupport(String token,String keyword, String model) throws Exception {

        Object res = new Object();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ContactDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getContactSupport?keyword="+keyword+"&model="+model)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ContactDTOResponse.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }

        return res;
    }

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

    public String mobileNumberMasking(String mobile) {
        String maskedNumberConfiguration = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MASKED_NUMBER_CONFIGURATION);
        if (Objects.equals(maskedNumberConfiguration, "true")) {
            if (mobile != null && !mobile.equalsIgnoreCase("")) {
                return mobile.replaceAll(".(?=.{4})", "*");
            }
        }
        return mobile;
    }

    public Object getBankNameByIFSC(String keyword) throws Exception {

        Object res = new Object();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, BankNameIFSCDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBankNameByIFSC?keyword=" + keyword)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(BankNameIFSCDTOResponse.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }


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

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, MultipartFile imageData, String userRefNo, String fileName, String clientId, String systemId) throws IOException {
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();


        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(imageData.getBytes());

        UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO = new UploadImageOnS3RequestDTO();
        UploadImageOnS3DataRequestDTO uploadImageOnS3DataRequestDTO = new UploadImageOnS3DataRequestDTO();
        UploadImageData uploadImageData = new UploadImageData();
        uploadImageData.setUserRefNo(userRefNo);
        uploadImageData.setFileContentType("");
        uploadImageData.setFileName(fileName);
        uploadImageData.setFile(base64);
        uploadImageOnS3DataRequestDTO.setData(uploadImageData);
        uploadImageOnS3DataRequestDTO.setSystemId(systemId);
        uploadImageOnS3DataRequestDTO.setClientId(clientId);
        uploadImageOnS3RequestDTO.setRequestData(uploadImageOnS3DataRequestDTO);
        uploadImageOnS3RequestDTO.setUserReferenceNumber("");


        log.info("uploadImageOnS3RequestDTO {}", uploadImageOnS3RequestDTO);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            log.info("imageData {}", imageData);
            res = HTTPRequestService.<Object, UploadImageOnS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/uploadImageOnS3")
                    .body(uploadImageOnS3RequestDTO)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UploadImageOnS3ResponseDTO.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    public DownloadBase64FromS3ResponseDTO downloadBase64FromS3(String token, String userRefNo, String fileName, String clientId, boolean isNativeFolder) throws IOException {
        DownloadBase64FromS3ResponseDTO res = new DownloadBase64FromS3ResponseDTO();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, DownloadBase64FromS3ResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBase64ByFileName?clientId=" + clientId + "&fileName=" + fileName + "&userRefNo=" + userRefNo + "&isNativeFolder=" + isNativeFolder)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(DownloadBase64FromS3ResponseDTO.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }


    public UploadImageOnS3ResponseDTO sendPdfToCustomerUsingS3(String token, MultipartFile imageData, String userRefNo, String clientId, String paymentMode, String receiptAmount, String fileName) throws IOException {
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();


        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(imageData.getBytes());

        UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO = new UploadImageOnS3RequestDTO();
        UploadImageOnS3DataRequestDTO uploadImageOnS3DataRequestDTO = new UploadImageOnS3DataRequestDTO();
        UploadImageData uploadImageData = new UploadImageData();
        uploadImageData.setUserRefNo(userRefNo);
        uploadImageData.setFileContentType("");
        uploadImageData.setFileName(fileName);
        uploadImageData.setFile(base64);
        uploadImageOnS3DataRequestDTO.setData(uploadImageData);
        uploadImageOnS3DataRequestDTO.setSystemId("collection");
        uploadImageOnS3DataRequestDTO.setClientId(clientId);
        uploadImageOnS3RequestDTO.setRequestData(uploadImageOnS3DataRequestDTO);
        uploadImageOnS3RequestDTO.setUserReferenceNumber("");
        String[] loanId = fileName.split("_");


        log.info("uploadImageOnS3RequestDTO {}", uploadImageOnS3RequestDTO);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            if(clientId.equals("finova")) {

                res = HTTPRequestService.<Object, UploadImageOnS3ResponseDTO>builder()
                        .httpMethod(HttpMethod.POST)
                        .url("http://localhost:1102/v1/uploadImageOnS3")
                        .body(uploadImageOnS3RequestDTO)
                        .httpHeaders(httpHeaders)
                        .typeResponseType(UploadImageOnS3ResponseDTO.class)
                        .build().call();

                log.info("upload image s3 for finova {}", res);



                ShortenUrlResponseDTO shortenUrlResponseDTO = new ShortenUrlResponseDTO();
                ShortenUrlRequestDTO shortenUrlRequestDTO = new ShortenUrlRequestDTO();
                ShortenUrlDataRequestDTO shortenUrlDataRequestDTO = new ShortenUrlDataRequestDTO();

                shortenUrlRequestDTO.setClientId(clientId);
                shortenUrlRequestDTO.setSystemId("collection");
                shortenUrlDataRequestDTO.setId(res.getData().getDownloadUrl());
                shortenUrlRequestDTO.setData(shortenUrlDataRequestDTO);

                log.info("shorten url request {}", shortenUrlRequestDTO);

                shortenUrlResponseDTO = HTTPRequestService.<Object, ShortenUrlResponseDTO>builder()
                        .httpMethod(HttpMethod.POST)
                        .url(SHORTEN_URL_UAT)
                        .body(shortenUrlRequestDTO)
                        .httpHeaders(httpHeaders)
                        .typeResponseType(ShortenUrlResponseDTO.class)
                        .build().call();

                log.info("shorten URL for finova {}", shortenUrlResponseDTO);

                FinovaSmsRequest finovaSmsRequest = new FinovaSmsRequest();
                if(paymentMode.equals("cash")) {
                    finovaSmsRequest.setFlow_id(FINOVA_CASH_MSG_FLOW_ID);
                } else if (paymentMode.equals("cheque")) {
                    finovaSmsRequest.setFlow_id(FINOVA_CHEQUE_MSG_FLOW_ID);
                } else {
                    finovaSmsRequest.setFlow_id(FINOVA_UPI_MSG_FLOW_ID);
                }
//                String[] loanId = fileName.split("_");
                finovaSmsRequest.setSender("FINOVA");
                finovaSmsRequest.setShort_url("0");
                finovaSmsRequest.setMobiles("917805951252");
                finovaSmsRequest.setAmount(receiptAmount);
                finovaSmsRequest.setLoanNumber(loanId[0]);
                finovaSmsRequest.setUrl(shortenUrlResponseDTO.getData().getResult());

                FinovaMsgDTOResponse finovaMsgDTOResponse = finovaSmsService.sendSmsFinova(finovaSmsRequest);
                log.info("sms service for finova {}", finovaMsgDTOResponse);
            }


            CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
            collectionActivityLogsEntity.setActivityName("send receipt message to user");
            collectionActivityLogsEntity.setActivityDate(new Date());
            collectionActivityLogsEntity.setDeleted(false);
            collectionActivityLogsEntity.setActivityBy(0L);
            collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
            collectionActivityLogsEntity.setAddress(res);
            collectionActivityLogsEntity.setRemarks(fileName);
            collectionActivityLogsEntity.setImages(res.getData());
            collectionActivityLogsEntity.setLoanId(Long.parseLong(loanId[0]));
            collectionActivityLogsEntity.setGeolocation(res);

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }

    public UserDetailByUserIdDTOResponse getUserDetailsByUserId(String token, Long userId) {
        UserDetailByUserIdDTOResponse res = new UserDetailByUserIdDTOResponse();

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, UserDetailByUserIdDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getUserDetailsByUserId?userId="+ userId)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(UserDetailByUserIdDTOResponse.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }
        return res;
    }
}
