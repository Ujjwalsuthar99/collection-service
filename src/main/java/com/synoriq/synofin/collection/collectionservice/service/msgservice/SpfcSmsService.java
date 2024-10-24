package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto.SpfcSmsRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos.SpfcMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.INTEGRATION_MSG_API_URL;

@Slf4j
@Service
public class SpfcSmsService {

    @Autowired
    private RestTemplate restTemplate;
    public SpfcMsgDTOResponse sendSmsSpfc(SpfcSmsRequestDTO spfcSmsRequestDTO, String token, String springProfile) throws Exception {

        SpfcMsgDTOResponse res;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");

        String url = INTEGRATION_MSG_API_URL;
        String prodUrl = Objects.equals(springProfile, "prod") ? url.replace("preprod", "prod2") : url;
        url = Objects.equals(springProfile, "uat") ? url.replace("preprod", springProfile) : prodUrl;
        log.info("integration url {}", url);

        res = HTTPRequestService.<Object, SpfcMsgDTOResponse>builder()
                .httpMethod(HttpMethod.POST)
                .url(url)
                .httpHeaders(httpHeaders)
                .body(spfcSmsRequestDTO)
                .typeResponseType(SpfcMsgDTOResponse.class)
                .build().call(restTemplate);


        return res;
    }
}
