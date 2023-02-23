package com.synoriq.synofin.collection.collectionservice.service.utilityservice;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Slf4j
@Builder
public class HTTPRequestService<T, S> {
    private HttpMethod httpMethod = HttpMethod.GET;
    private String url;
    private T body;
    private HttpHeaders httpHeaders;
    private Class<S> typeResponseType = (Class<S>) String.class;

    public S call() throws Exception {
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
            throw new Exception("Request returned with following Response code: "
                    + response.getStatusCodeValue() + " Response Body: " + response.getBody());
        }
    }
    public S updatedcall() throws Exception {
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
            throw new Exception("Request returned with following Response code: "
                    + response.getStatusCodeValue() + " Response Body: " + response.getBody());
        }
    }
}
