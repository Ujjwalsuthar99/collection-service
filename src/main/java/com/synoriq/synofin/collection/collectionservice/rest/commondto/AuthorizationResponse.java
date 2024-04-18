package com.synoriq.synofin.collection.collectionservice.rest.commondto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationResponse {

    @JsonProperty("response")
    private Boolean response;
    @JsonProperty("user_data")
    private Object userData;
    @JsonProperty("auth_data")
    private AuthorizationAuthData authData;
    @JsonProperty("error_message")
    private String errorMessage;
}
