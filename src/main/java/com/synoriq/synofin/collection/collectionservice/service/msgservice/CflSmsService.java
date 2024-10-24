package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto.CflSmsRequest;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos.CflMsgDTOResponse;
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
public class CflSmsService {

    @Autowired
    private RestTemplate restTemplate;
    public CflMsgDTOResponse sendSmsCfl(CflSmsRequest cflSmsRequest, String token, String springProfile) throws Exception {
        try {
            CflMsgDTOResponse res;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");
            String url = INTEGRATION_MSG_API_URL;
            String prodUrl = Objects.equals(springProfile, "prod") ? url.replace("preprod", "prod2") : url;
            url = Objects.equals(springProfile, "uat") ? url.replace("preprod", springProfile) : prodUrl;
            log.info("integration url {}", url);

            res = HTTPRequestService.<Object, CflMsgDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(url)
                    .httpHeaders(httpHeaders)
                    .body(cflSmsRequest)
                    .typeResponseType(CflMsgDTOResponse.class)
                    .build().call(restTemplate);


            return res;
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

    }
}




