package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeStatusCheckDataRequestDTO {

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("customerId")
    private String customerId;

}
