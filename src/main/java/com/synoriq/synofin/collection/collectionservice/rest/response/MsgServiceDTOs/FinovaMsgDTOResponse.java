package com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class FinovaMsgDTOResponse {
    @JsonProperty("response")
    public String message;

    @JsonProperty("data")
    public String type;

}

