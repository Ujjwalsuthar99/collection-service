package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.FinovaSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.FinovaMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FINOVA_MSG_API_URL;

@Slf4j
@Service
public class FinovaSmsService {
    public FinovaMsgDTOResponse sendSmsFinova(FinovaSmsRequest finovaSmsRequest) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authkey", "298176AIYdOXVbdzhe5d9ef9b5");


        FinovaMsgDTOResponse res = HTTPRequestService.<Object, FinovaMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(FINOVA_MSG_API_URL)
                .httpHeaders(httpHeaders)
                .body(finovaSmsRequest)
                .typeResponseType(FinovaMsgDTOResponse.class)
                .build().call();

        return res;
    }
}
