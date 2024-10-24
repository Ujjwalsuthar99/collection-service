package com.synoriq.synofin.collection.collectionservice.rest.response.userdetailsbyuseriddtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDataReturnResponseDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("name")
    private String name;

}
