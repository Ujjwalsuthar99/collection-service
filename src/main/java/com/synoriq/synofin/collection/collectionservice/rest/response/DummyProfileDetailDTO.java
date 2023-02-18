package com.synoriq.synofin.collection.collectionservice.rest.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DummyProfileDetailDTO {
    @JsonProperty("name")
    public String name;

    @JsonProperty("email")
    public String email;

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("mobile")
    public String mobile;



}
