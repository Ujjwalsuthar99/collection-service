package com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyTransactionDTO {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("sso_token")
    private String ssoToken;

}
