package com.synoriq.synofin.collection.collectionservice.service.msgservice;

import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.CSL_MSG_API_URL;

@Slf4j
@Service
public class CslSmsService {
    public String sendSmsCsl(String postBody) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add("Cookie", "JSESSIONID=D52F7C9914A1B8C31622CAD0E1400C08; JSESSIONID=803E40A8BDBE4B66041D37918607F71E");

        log.info("csl sms post body {}", postBody);

        String res = null;
        res = HTTPRequestService.<Object, String>builder()
                .httpMethod(HttpMethod.POST)
                .url(CSL_MSG_API_URL)
                .httpHeaders(httpHeaders)
                .body(postBody)
                .typeResponseType(String.class)
                .build().call();

        return res;
    }
}
