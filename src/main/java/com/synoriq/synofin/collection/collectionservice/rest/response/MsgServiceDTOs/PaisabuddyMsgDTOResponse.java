package com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PaisabuddyMsgDTOResponse {
    @JsonProperty("response")
    public String message;

    @JsonProperty("data")
    public String type;

}

