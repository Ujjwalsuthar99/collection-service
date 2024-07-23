package com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchDtoRequest {

    @JsonProperty("data")
    private requestDataDTO requestData;


    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}
