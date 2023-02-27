package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class UserDTOResponse {
    @JsonProperty("response")
    public String response;

    @JsonProperty("data")
    public List<Object> data;

    @JsonProperty("error")
    public String error;

}

