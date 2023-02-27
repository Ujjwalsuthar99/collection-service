package com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.requestDataDTO;
import lombok.Data;

@Data
public class MasterDtoRequest {

    @JsonProperty("data")
    private requestDataDTO requestData;


    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}

