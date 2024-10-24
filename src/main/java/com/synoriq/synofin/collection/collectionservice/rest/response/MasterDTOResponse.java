package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MasterDTOResponse {
    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public Object data;

    @JsonProperty("error")
    public Object error;
}

