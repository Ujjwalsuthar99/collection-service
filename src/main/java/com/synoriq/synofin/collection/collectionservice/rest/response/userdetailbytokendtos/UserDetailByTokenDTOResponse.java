package com.synoriq.synofin.collection.collectionservice.rest.response.userdetailbytokendtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class UserDetailByTokenDTOResponse {
    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public UserDetailDataByTokenDTO data;

    @JsonProperty("error")
    public String error;

}

