package com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PaisabuddyMsgDTOResponse {
    @JsonProperty("message")
    public String message;

    @JsonProperty("type")
    public String type;

}

