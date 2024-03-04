package com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDetailDataByTokenDTO {

    @JsonProperty("user_type")
    private String userType;

    @JsonProperty("user_data")
    private UserDataDTO userData;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("username")
    private String userName;

}
