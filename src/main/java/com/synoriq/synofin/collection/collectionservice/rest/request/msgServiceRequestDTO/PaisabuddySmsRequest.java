package com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaisabuddySmsRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("sender")
    String sender;

    @JsonProperty("var1")
    String customerName;

    @JsonProperty("var2")
    String amount;

    @JsonProperty("var4")
    String receiptId;

    @JsonProperty("var5")
    String loanNumber;

    @JsonProperty("var6")
    String shortenUrl;

    @JsonProperty("mobiles")
    String mobiles;
}
