package com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos;

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

    @JsonProperty("validation_error")
    public Object validationError;

}
