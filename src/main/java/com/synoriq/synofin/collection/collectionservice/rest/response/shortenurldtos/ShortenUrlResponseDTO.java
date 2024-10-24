package com.synoriq.synofin.collection.collectionservice.rest.response.shortenurldtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShortenUrlResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public ShortenUrlDataResponseDTO data;

    @JsonProperty("request_id")
    public String requestId;
}
