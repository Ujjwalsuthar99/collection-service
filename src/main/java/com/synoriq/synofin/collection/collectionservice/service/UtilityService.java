package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3DataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UtilityService {

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

    public UploadImageOnS3ResponseDTO uploadImageOnS3(String token, UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO, MultipartFile imageData) {
        UploadImageOnS3ResponseDTO res = new UploadImageOnS3ResponseDTO();

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
}
