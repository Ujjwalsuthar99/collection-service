package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class BankNameIFSCDTOResponse {
    @JsonProperty("response")
    public String response;

    @JsonProperty("data")
    public Object data;

    @JsonProperty("error")
    public String error;

}

