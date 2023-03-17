package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@NoArgsConstructor
public class ReceiptServiceResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public Object data;

    @JsonProperty("error")
    public String error;

}
