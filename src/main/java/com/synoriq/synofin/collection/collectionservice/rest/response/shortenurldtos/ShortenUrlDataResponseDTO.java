package com.synoriq.synofin.collection.collectionservice.rest.response.shortenurldtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShortenUrlDataResponseDTO {

    @JsonProperty("action")
    public String action;

    @JsonProperty("result")
    public String result;
}
