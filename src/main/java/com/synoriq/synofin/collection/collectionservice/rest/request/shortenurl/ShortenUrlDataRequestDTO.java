package com.synoriq.synofin.collection.collectionservice.rest.request.shortenurl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShortenUrlDataRequestDTO {

    @JsonProperty("id")
    public String id;
}
