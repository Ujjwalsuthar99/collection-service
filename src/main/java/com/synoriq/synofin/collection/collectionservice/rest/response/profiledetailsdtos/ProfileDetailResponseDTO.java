package com.synoriq.synofin.collection.collectionservice.rest.response.profiledetailsdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfileDetailResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public ProfileDataDTO data;

    @JsonProperty("error")
    public String error;






}
