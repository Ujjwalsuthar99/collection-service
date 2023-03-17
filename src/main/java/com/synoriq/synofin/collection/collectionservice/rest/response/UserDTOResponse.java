package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import lombok.Data;

import java.util.List;


@Data
public class UserDTOResponse {
    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public List<UsersDataDTO> data;

    @JsonProperty("error")
    public String error;

}

