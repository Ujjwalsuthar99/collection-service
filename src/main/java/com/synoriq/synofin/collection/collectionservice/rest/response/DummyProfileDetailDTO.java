package com.synoriq.synofin.collection.collectionservice.rest.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DummyProfileDetailDTO {

    @JsonProperty("name")
    public String name;

    @JsonProperty("mobile")
    public String mobile;

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("email")
    public String email;






}
