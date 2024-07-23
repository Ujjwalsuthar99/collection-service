package com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class CflMsgDTOResponse {
    @JsonProperty("response")
    public String message;

    @JsonProperty("data")
    public ResponseDataDTO data;

    @JsonProperty("request_id")
    private String requestId;
}
