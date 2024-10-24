package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto.PaisabuddySmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos.PaisabuddyMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.PAISABUDDY_MSG_API_URL;

@Slf4j
@Service
public class PaisabuddySmsService {

    @Autowired
    private RestTemplate restTemplate;
    public PaisabuddyMsgDTOResponse sendSmsPaisabuddy(PaisabuddySmsRequest paisabuddySmsRequest) throws CustomException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authkey", "370987A8VWXsHTIO61c2ba97P1");


       return HTTPRequestService.<Object, PaisabuddyMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(PAISABUDDY_MSG_API_URL)
                .httpHeaders(httpHeaders)
                .body(paisabuddySmsRequest)
                .typeResponseType(PaisabuddyMsgDTOResponse.class)
                .build().call(restTemplate);


    }
}
