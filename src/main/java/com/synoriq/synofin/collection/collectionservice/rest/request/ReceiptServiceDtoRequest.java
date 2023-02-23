package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.requestDataDTO;
import lombok.Data;

@Data
public class ReceiptServiceDtoRequest {

    @JsonProperty("data")
    private requestDataDTO requestData;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

}
