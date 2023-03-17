package com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ReceiptServiceSystemPropertiesResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public ReceiptDateResponse data;

    @JsonProperty("error")
    public String error;

}
