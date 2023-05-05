package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CollectionLimitUserWiseDtoRequest {

    @JsonProperty("cash")
    private Double cash;

    @JsonProperty("cheque")
    private Double cheque;

    @JsonProperty("upi")
    private Double upi;
}
