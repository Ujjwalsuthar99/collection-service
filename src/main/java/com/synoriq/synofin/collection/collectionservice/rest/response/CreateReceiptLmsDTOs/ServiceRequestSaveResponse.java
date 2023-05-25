package com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceRequestSaveResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public ServiceRequestIdResponse data;

    @JsonProperty("error")
    public ServiceRequestErrorResponse error;

}
