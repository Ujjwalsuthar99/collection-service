package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptServiceResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@Slf4j
@Builder
public class ReceiptService {

    @Autowired
    private LoanAllocationRepository loanAllocationRepository;


    public Object createReceipt(@RequestBody ReceiptServiceDtoRequest receiptServiceDtoRequest, String bearerToken) throws Exception {
        Object res = new Object();
         ReceiptServiceDtoRequest createReceiptBody = new ObjectMapper().convertValue(receiptServiceDtoRequest, ReceiptServiceDtoRequest.class);
         log.info("createReceiptBody {}", createReceiptBody);
        try {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ReceiptServiceResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:8070/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(createReceiptBody)
                    .typeResponseType(ReceiptServiceResponse.class)
                    .build().call();


//            baseResponse = new BaseDTOResponse<Object>(baseResponse);
        } catch (Exception ee) {
            throw new Exception(ee);
        }
        return res;
    }


    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception {
        List<LoanAllocationEntity> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getLoansByAllocatedToUserId(allocatedToUserId);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return loanAllocationEntities;
    }

}