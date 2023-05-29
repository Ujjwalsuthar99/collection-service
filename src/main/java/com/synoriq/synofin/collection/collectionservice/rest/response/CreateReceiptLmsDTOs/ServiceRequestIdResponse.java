package com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceRequestIdResponse {

    @JsonProperty("result")
    public String data;

    @JsonProperty("service_request_id")
    public Long serviceRequestId;

}
