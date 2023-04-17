package com.synoriq.synofin.collection.collectionservice.rest.response.DownloadS3Base64DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DownloadBase64FromS3 {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public String data;

    @JsonProperty("error")
    public Object error;
}
