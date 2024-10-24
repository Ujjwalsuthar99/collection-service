package com.synoriq.synofin.collection.collectionservice.rest.request.masterdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MasterDtoRequest {

    @JsonProperty("data")
    private RequestDataDTO requestData;


    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}

