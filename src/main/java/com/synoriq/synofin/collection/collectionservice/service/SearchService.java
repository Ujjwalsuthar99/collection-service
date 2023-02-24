package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.SearchDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchService {

    public Object getLoanDataBySearch(SearchDtoRequest requestBody) throws Exception {
//        SearchDTOResponse res = new SearchDTOResponse();
        Object res = new Object();
        SearchDtoRequest searchBody = new ObjectMapper().convertValue(requestBody, SearchDtoRequest.class);
        log.info("Search Body -- {} ", searchBody.getRequestData().getSearchTerm());
        try {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, SearchDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getLoanDataBySearch")
                    .httpHeaders(httpHeaders)
                    .body(searchBody)
                    .typeResponseType(SearchDTOResponse.class)
                    .build().call();

            log.info("responseData {}", res);
        } catch (Exception ee) {
//            ee.printStackTrace();
            log.error("{}", ee.getMessage());
        }

        return res;
    }
}
