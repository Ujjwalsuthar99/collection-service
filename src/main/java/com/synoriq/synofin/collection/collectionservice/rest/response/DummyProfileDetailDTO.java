package com.synoriq.synofin.collection.collectionservice.rest.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DummyProfileDetailDTO {

    @JsonProperty("response")
    public String response;

    @JsonProperty("data")
    public ProfileDataDTO data;

    @JsonProperty("error")
    public String error;






}
