package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.CflSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO.LifcSmsRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.CflMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs.LifcMsgDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.INTEGRATION_MSG_API_URL;

@Slf4j
@Service
public class LifcSmsService {
    public LifcMsgDTOResponse sendSmsLifc(LifcSmsRequest lifcSmsRequest, String token, String springProfile) throws Exception {
        try {
            LifcMsgDTOResponse res;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");
            String url = INTEGRATION_MSG_API_URL;
            url = Objects.equals(springProfile, "uat") ? url.replace("preprod", springProfile) : (Objects.equals(springProfile, "prod") ? url.replace("preprod", "prod2") : url);
            log.info("integration url {}", url);

            res = HTTPRequestService.<Object, LifcMsgDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url(url)
                    .httpHeaders(httpHeaders)
                    .body(lifcSmsRequest)
                    .typeResponseType(LifcMsgDTOResponse.class)
                    .build().call();


            return res;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}




