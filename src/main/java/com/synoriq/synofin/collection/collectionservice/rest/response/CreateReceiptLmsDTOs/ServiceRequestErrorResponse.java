package com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceRequestErrorResponse {

    @JsonProperty("code")
    public String code;

    @JsonProperty("text")
    public String text;

}
