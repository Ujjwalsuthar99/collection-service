package com.synoriq.synofin.collection.collectionservice.rest.response.userDetailsByUserIdDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDetailByUserIdDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public UserDetailDataByUserIdDTO data;

    @JsonProperty("error")
    public String error;

}
