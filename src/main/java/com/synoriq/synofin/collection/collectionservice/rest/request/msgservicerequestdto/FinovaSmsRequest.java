package com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FinovaSmsRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("sender")
    String sender;

    @JsonProperty("short_url")
    String shortUrl;

    @JsonProperty("mobiles")
    String mobiles;

    @JsonProperty("var1")
    String amount;

    @JsonProperty("var2")
    String loanNumber;

    @JsonProperty("var3")
    String url;

}
