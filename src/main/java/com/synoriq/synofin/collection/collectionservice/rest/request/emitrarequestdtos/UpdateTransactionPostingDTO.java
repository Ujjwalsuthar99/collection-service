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
public class UpdateTransactionPostingDTO {

    @JsonProperty("application_id")
    private String applicationId;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("sso_token")
    private String ssoToken;

}
