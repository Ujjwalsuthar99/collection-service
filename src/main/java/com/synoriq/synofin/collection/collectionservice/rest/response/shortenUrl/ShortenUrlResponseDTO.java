package com.synoriq.synofin.collection.collectionservice.rest.response.shortenUrl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.shortenUrl.ShortenUrlDataRequestDTO;
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
