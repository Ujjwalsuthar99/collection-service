package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.SpfcSmsRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.SpfcMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FINOVA_MSG_API_URL;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.INTEGRATION_MSG_API_URL;

@Slf4j
@Service
public class SpfcSmsService {
    public SpfcMsgDTOResponse sendSmsSpfc(SpfcSmsRequestDTO spfcSmsRequestDTO, String token, String springProfile) throws Exception {

        SpfcMsgDTOResponse res;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");

        String url = INTEGRATION_MSG_API_URL;
        url = springProfile == "uat" ? url.replace("preprod", springProfile) : (springProfile == "prod" ? url.replace("preprod", "prod2") : url);
        log.info("integration url {}", url);

        res = HTTPRequestService.<Object, SpfcMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(url)
                .httpHeaders(httpHeaders)
                .body(spfcSmsRequestDTO)
                .typeResponseType(SpfcMsgDTOResponse.class)
                .build().call();


        return res;
    }
}
