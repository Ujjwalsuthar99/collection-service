package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ContactDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MasterDataService {

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
            int pageRequest = (page * size) - 10 ;
            List<UsersDataDTO> pageableArr = new LinkedList<>();
            for (int i = pageRequest; i < (pageRequest+10); i++) {
                pageableArr.add(userData.get(i));
            }
//            List<UsersDataDTO> filteredList = userData.parallelStream().filter(user -> (user.getUsername().contains(key) || user.getName().contains(key))).collect(Collectors.toList());
            if (key.equals("")) {
                baseDTOResponse = new BaseDTOResponse<>(pageableArr);
            } else {
                List<UsersDataDTO> filteredList = userData.
                                                stream().
                                                filter(user -> ( Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getUsername()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getName()).find() || Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(user.getEmployeeCode()).find())).
                                                collect(Collectors.toList());
                log.info("filteredList {}", filteredList);
                baseDTOResponse = new BaseDTOResponse<>(filteredList);
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

}
