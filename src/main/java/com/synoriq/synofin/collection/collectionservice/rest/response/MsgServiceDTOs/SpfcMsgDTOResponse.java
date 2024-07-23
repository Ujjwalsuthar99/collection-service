package com.synoriq.synofin.collection.collectionservice.rest.response.MsgServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class SpfcMsgDTOResponse {

    @JsonProperty("response")
    public String message;

    @JsonProperty("data")
    public ResponseDataDTO data;

    @JsonProperty("request_id")
    private String requestId;

}
