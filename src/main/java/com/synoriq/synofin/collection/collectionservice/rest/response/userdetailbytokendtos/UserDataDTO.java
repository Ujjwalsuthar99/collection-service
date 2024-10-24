package com.synoriq.synofin.collection.collectionservice.rest.response.userdetailbytokendtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserDataDTO {

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("email")
    private String email;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("name")
    private String name;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("roles")
    private List<Integer> roles;

    @JsonProperty("permissions")
    private List<String> permissions;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("is_2fa_enabled")
    private Boolean is2FaEnabled;
}
