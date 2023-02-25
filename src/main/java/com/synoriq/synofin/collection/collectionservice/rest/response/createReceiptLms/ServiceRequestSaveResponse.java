package com.synoriq.synofin.collection.collectionservice.rest.response.createReceiptLms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties.ReceiptDateResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ServiceRequestSaveResponse {

    @JsonProperty("response")
    public String response;

    @JsonProperty("data")
    public ServiceRequestIdResponse data;

    @JsonProperty("error")
    public Object error;

}
