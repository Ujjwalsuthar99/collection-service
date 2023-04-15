package com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShortenUrlRequestDTO {

    @JsonProperty("data")
    public ShortenUrlDataRequestDTO data;

    @JsonProperty("client_id")
    public String clientId;

    @JsonProperty("system_id")
    public String systemId;
}
