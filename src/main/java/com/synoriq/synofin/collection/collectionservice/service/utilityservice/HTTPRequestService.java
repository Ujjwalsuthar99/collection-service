package com.synoriq.synofin.collection.collectionservice.service.utilityservice;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;

@Slf4j
@Builder
public class HTTPRequestService<T, S> {
    private HttpMethod httpMethod = HttpMethod.GET;
    private String url;
    private T body;
    private HttpHeaders httpHeaders;
    private Class<S> typeResponseType = (Class<S>) String.class;
    private static final String EXCEPTION_STR = "Request returned with following Response code: ";
    private static final String RESP_BODY_STR = " Response Body: ";

    public S call() throws CustomException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<S> response = restTemplate.exchange(
                url,
                httpMethod,
                new HttpEntity<>(body, httpHeaders),
                typeResponseType
        );
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            return response.getBody();
        } else {
            throw new CustomException(EXCEPTION_STR
                    + response.getStatusCodeValue() + RESP_BODY_STR + response.getBody());
        }
    }

    public S call(RestTemplate restTemplate) throws CustomException {
        ResponseEntity<S> response = restTemplate.exchange(
                url,
                httpMethod,
                new HttpEntity<>(body, httpHeaders),
                typeResponseType
        );
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            return response.getBody();
        } else {
            throw new CustomException(EXCEPTION_STR
                    + response.getStatusCodeValue() + RESP_BODY_STR + response.getBody());
        }
    }
    public S updatedcall() throws CustomException {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        ResponseEntity<S> response = restTemplate.exchange(
                url,
                httpMethod,
                new HttpEntity<>(body, httpHeaders),
                typeResponseType
        );
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            return response.getBody();
        } else {
            throw new CustomException(EXCEPTION_STR
                    + response.getStatusCodeValue() + RESP_BODY_STR + response.getBody());
        }
    }
}
