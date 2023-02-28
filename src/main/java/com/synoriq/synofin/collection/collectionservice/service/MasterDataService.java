package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MasterDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public Object getUserDetail(String token, Integer page, Integer size) throws Exception {

        UserDTOResponse res = new UserDTOResponse();
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

            log.info("responseData {}", res);
            List<Object> userData = res.getData();
            log.info("userData.toArray().length {}", userData.toArray().length);
//            for (int i = 0; i < userData.toArray().length; i++) {
//                log.info("userData Object {}", userData);
//            }

        } catch (Exception ee) {
            log.error("{}", ee.getMessage());
        }

        return res;
    }

}
