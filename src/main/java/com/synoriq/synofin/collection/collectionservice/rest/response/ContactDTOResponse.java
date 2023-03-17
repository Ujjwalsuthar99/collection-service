package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;



@Data
public class ContactDTOResponse {
    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public List<Object> data;

    @JsonProperty("error")
    public String error;

}

