package com.synoriq.synofin.collection.collectionservice.rest.response.msgservicedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ResponseDataDTO {

    @JsonProperty("statusResult")
    public String statusResult;

    @JsonProperty("status")
    public String status;

    @JsonProperty("sms-response-details")
    private Object smsResponseDetails;

}

