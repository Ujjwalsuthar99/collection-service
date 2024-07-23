package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.FinovaSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.PaisabuddySmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.FinovaMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.PaisabuddyMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FINOVA_MSG_API_URL;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.PAISABUDDY_MSG_API_URL;

@Slf4j
@Service
public class PaisabuddySmsService {
    public PaisabuddyMsgDTOResponse sendSmsPaisabuddy(PaisabuddySmsRequest paisabuddySmsRequest) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authkey", "370987A8VWXsHTIO61c2ba97P1");


        PaisabuddyMsgDTOResponse res = HTTPRequestService.<Object, PaisabuddyMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(PAISABUDDY_MSG_API_URL)
                .httpHeaders(httpHeaders)
                .body(paisabuddySmsRequest)
                .typeResponseType(PaisabuddyMsgDTOResponse.class)
                .build().call();


        return res;
    }
}
