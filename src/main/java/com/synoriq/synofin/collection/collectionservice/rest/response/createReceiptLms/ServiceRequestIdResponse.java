package com.synoriq.synofin.collection.collectionservice.rest.response.createReceiptLms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ServiceRequestIdResponse {

    @JsonProperty("result")
    public String data;

    @JsonProperty("service_request_id")
    public Long serviceRequestId;

}
