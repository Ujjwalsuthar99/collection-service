package com.synoriq.synofin.collection.collectionservice.rest.request.searchdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchDtoRequest {

    @JsonProperty("data")
    private RequestDataDTO requestData;


    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}
