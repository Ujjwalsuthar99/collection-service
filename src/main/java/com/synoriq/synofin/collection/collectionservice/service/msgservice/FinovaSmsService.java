package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto.FinovaSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos.FinovaMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FINOVA_MSG_API_URL;

@Slf4j
@Service
public class FinovaSmsService {
    @Autowired
    private RestTemplate restTemplate;
    public FinovaMsgDTOResponse sendSmsFinova(FinovaSmsRequest finovaSmsRequest) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authkey", "298176AIYdOXVbdzhe5d9ef9b5");


        return HTTPRequestService.<Object, FinovaMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(FINOVA_MSG_API_URL)
                .httpHeaders(httpHeaders)
                .body(finovaSmsRequest)
                .typeResponseType(FinovaMsgDTOResponse.class)
                .build().call(restTemplate);


    }
}
