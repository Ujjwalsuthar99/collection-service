package com.synoriq.synofin.collection.collectionservice.rest.response.msgServiceResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class FinovaMsgDTOResponse {
    @JsonProperty("response")
    public String message;

    @JsonProperty("data")
    public String type;

}

