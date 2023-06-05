package com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FinovaSmsRequest {

    @JsonProperty("flow_id")
    private String flowId;

    @JsonProperty("sender")
    String sender;

    @JsonProperty("short_url")
    String shortUrl;

    @JsonProperty("mobiles")
    String mobiles;

//    @JsonProperty("amount")
//    String amount;
//
//    @JsonProperty("loanNumber")
//    String loanNumber;
//
//    @JsonProperty("url")
//    String url;

    @JsonProperty("val1")
    String amount;

    @JsonProperty("val2")
    String loanNumber;

    @JsonProperty("val3")
    String url;

}
